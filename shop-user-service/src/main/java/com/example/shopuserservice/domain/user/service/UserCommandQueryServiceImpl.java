package com.example.shopuserservice.domain.user.service;

import com.example.commondto.events.ServiceNames;
import com.example.commondto.format.DateFormat;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.client.OrderServiceClient;
import com.example.shopuserservice.config.AsyncConfig;
import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransactions;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.redisrepository.UserTransactionRedisRepository;
import com.example.shopuserservice.web.dto.UserDto;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorCode;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.util.reactor.Reactor;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseOrder;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.example.shopuserservice.web.error.ErrorCode.*;

@Slf4j
@Service
public class UserCommandQueryServiceImpl implements UserCommandQueryService {
    private final UserRepository userRepository;
    private final UserTransactionRedisRepository userTransactionRedisRepository;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    private final TransactionTemplate transactionTemplate;
    private final Executor serviceExecutor;
    private final HikariDataSource hikariDataSource;
    private final PasswordEncoder pwe;
    private final OrderServiceClient orderServiceClient;

    public UserCommandQueryServiceImpl(UserRepository userRepository,
                                       UserTransactionRedisRepository userTransactionRedisRepository, @Qualifier("taskExecutor") Executor serviceExecutor,
                                       HikariDataSource hikariDataSource,
                                       @Qualifier("bcrypt") PasswordEncoder pwe,
                                       OrderServiceClient orderServiceClient,
                                       KafkaTemplate<String, Object> kafkaProducerTemplate, TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.userTransactionRedisRepository = userTransactionRedisRepository;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
        this.pwe = pwe;
        this.orderServiceClient = orderServiceClient;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.transactionTemplate = transactionTemplate;
    }



    // 유저 검색
    @Override
    public CompletableFuture<Optional<User>> getUserById(String id) {
        return CompletableFuture.supplyAsync(()->{
            return userRepository.findById(id);
        });
    }

    @Override
    public CompletableFuture<UserTransactions> updateStatus(UserResponseEvent event) {
        return CompletableFuture.supplyAsync(()->{
            UserTransactions userTransactions = userTransactionRedisRepository
                    .findById(event.getEventId())
                    .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

            userTransactions = changeUserRegisterStatusByKafkaEvent(event, userTransactions);   // redis status update
            checkCurrentStatus(userTransactions,event.getUserId()); // 스레드 공통 전파
            return userTransactionRedisRepository.save(userTransactions);   // redis status save
        });
    }

    private UserTransactions changeUserRegisterStatusByKafkaEvent(UserResponseEvent event, UserTransactions userTransactions) {
        switch (event.getServiceName()){
            case ServiceNames.chat -> {
                userTransactions.setChatStatus(event.getUserResponseStatus());
                break;
            }
            case ServiceNames.customer -> {
                userTransactions.setCustomerStatus(event.getUserResponseStatus());
                break;
            }
        }
        return userTransactions;
    }

    /**
     * userTransaction 에 따라서
     *
     * @param userTransactions
     * @param userId
     */
    private void checkCurrentStatus(UserTransactions userTransactions,String userId) {
        if (!userTransactions.getChatStatus().equals(UserResponseStatus.USER_APPEND.name())
                && !userTransactions.getCustomerStatus().equals(UserResponseStatus.USER_APPEND.name())
                && !userTransactions.getUserStatus().equals(UserStatus.USER_INSERT.name())
                && !userTransactions.getUserStatus().equals(UserStatus.USER_DELETE.name())){
            Reactor.emitAndComplete(userId, userTransactions);
        } else{
            Reactor.emit(userId, userTransactions);
        }
    }

    // 유저 저장
    @Override
    public CompletableFuture<User> createUser(RequestUser request, UUID eventId) {
        return CompletableFuture.supplyAsync(()->{
            User user = User.builder()
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .userPw(pwe.encode(request.getUserPw()))
                    .userId(request.getUserId())
                    .loginDate(DateFormat.getCurrentTime())
                    .logoutDate(DateFormat.getCurrentTime())
                    .build();

            return transactionTemplate.execute((status) -> {
                return userRepository.save(user);
            });
        });
    }

    @Override
    public CompletableFuture<UserTransactions> newUserEvent(RequestUser req, UUID eventId, UserEvent userEvent) {
        return CompletableFuture.supplyAsync(()->{
            return transactionTemplate.execute((status)->{

                UserTransactions userTransaction = createUserTransaction(req, eventId);
                // userTransaction 1차 전송
                Reactor.emit(req.getUserId(), userTransaction);

                // 이벤트 Transaction 저장
                LocalDateTime now1 = LocalDateTime.now();
                UserTransactions ut = userTransactionRedisRepository.save(userTransaction);
                LocalDateTime now2 = LocalDateTime.now();

                Optional<User> findUser = userRepository.findById(ut.getUserId());
                if (findUser.isEmpty()){

                    // 유저 저장
                    User user = User.builder()
                            .userName(ut.getUserName())
                            .email(ut.getEmail())
                            .role(ut.getRole())
                            .userPw(ut.getUserPw())
                            .userId(ut.getUserId())
                            .loginDate(DateFormat.getCurrentTime())
                            .logoutDate(DateFormat.getCurrentTime())
                            .build();

                    userRepository.save(user);
                    ut.setUserStatus(UserStatus.USER_INSERT_COMPLETE.name());
                    log.debug("User save success");
                } else {
                    log.debug("Already user exist in authentication server");
                    ut.setUserStatus(UserStatus.USER_DUPLICATION.name());
                }

                userTransactionRedisRepository.save(ut);

                log.debug("Now send event to other server");
                sendToKafkaWithKey(
                        KafkaTopic.userReq,
                        userEvent,
                        req.getUserId()
                ).thenRun(()->{
                    log.debug("Redis saving time : "+Duration.between(now1, now2).toMillis() +
                            "ms, Kafka sending Time : "+Duration.between(now2, LocalDateTime.now()).toMillis()+"ms");
                });

                return ut;
            });
        });
    }

    @NotNull
    private UserTransactions createUserTransaction(RequestUser req, UUID eventId) {
        return new UserTransactions(
                eventId,
                UserStatus.USER_INSERT,
                UserResponseStatus.USER_APPEND,
                UserResponseStatus.USER_APPEND,
                req.getUserId(),
                DateFormat.getCurrentTime(),
                req.getEmail(),
                req.getUserName(),
                pwe.encode(req.getUserPw()),
                req.getRole());
    }

    private CompletableFuture<?> sendToKafkaWithKey(String topic,Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }

    // 로그인
    @Override
    public DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr) {

        return dr;
    }

    // 로그인
    @Override
    public DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr) {

        return dr;
    }

    private void queryWithMethods(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr, CompletableFuture<?> cf) {
        CompletableFuture.runAsync(()->{
            try {
                cf.get(); // Blocking
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        },serviceExecutor).thenRunAsync(()->{
            dr.setResult(ResponseEntity.ok("success"));
        },serviceExecutor).exceptionally(e->{
            if (e.getCause().getCause() instanceof CustomException){
                dr.setResult(ErrorResponse.toResponseEntity(((CustomException) e.getCause().getCause()).getErrorCode()));
            } else {
                dr.setResult(ResponseEntity.badRequest().body("default bad request response"));
            }
            return null;
        });
    }


//    // 유저 삭제
//    @Override
//    @Transactional
//    @Async
//    public CompletableFuture<Boolean> removeUser(UUID eventId, String userId) {
//
//        // tx 저장
//        userTransactionRepository.save(
//                new UserTransaction(
//                        eventId,
//                        UserStatus.USER_DELETE,
//                        UserResponseStatus.USER_APPEND,
//                        UserResponseStatus.USER_APPEND,
//                        userId,
//                        LocalDateTime.now(),
//                        null,
//                        null,
//                        null,
//                        null)
//        );
//
//        Optional<User> findUser = userRepository.findById(userId);
//
//        if (findUser.isPresent()){
//            userRepository.delete(findUser.get());
//            return CompletableFuture.completedFuture(true);
//        }
//
//        throw new CustomException(CANNOT_FIND_USER);
//    }

    // 유저 업데이트
    @Override
    public void updateUser(User user) {
        Optional<User> findUser = userRepository.findById(user.getUserId());
        if (findUser.isPresent()){
            findUser.get().setUserName(user.getUserName());
            findUser.get().setUserId(user.getUserId());
            findUser.get().setUserPw(user.getUserPw());
            findUser.get().setEmail(user.getEmail());
        }
        throw new CustomException(CANNOT_FIND_USER);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<UserDto> getUserDetailsByUserId(String username) {
        log.info("getUserDetailsByUserId");
        Optional<User> user = userRepository.findById(username);
        List<ResponseOrder> orders = null;

        if (!user.isPresent()){
            throw new UsernameNotFoundException(username);
        }
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        try{
            orders = orderServiceClient.getOrders(username);
        }catch (Exception e){}

        userDto.setOrders(orders);
        return CompletableFuture.completedFuture(userDto);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<String> changePassword(String userId, String userPw) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            findUser.get().setUserPw(pwe.encode(userPw));
            return CompletableFuture.completedFuture("success");
        } else {
            throw new UsernameNotFoundException("cannot find user");
        }
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<List<User>> getAllUser() {
        List<User> allUser = userRepository.findAll();
        return CompletableFuture.completedFuture(allUser);
    }

    private void printHikariCPInfo() {
        log.info(String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        ));
    }
}
