package com.example.shopuserservice.domain.user.service;

import static com.example.commondto.error.ErrorCode.CANNOT_FIND_USER;

import com.example.commondto.error.CustomException;
import com.example.commondto.format.DateFormat;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.redisrepository.UserTransactionRedisRepository;
import com.example.commondto.dto.user.UserDto;
import com.example.shopuserservice.domain.user.service.modules.UserRedisManager;
import com.example.shopuserservice.domain.user.service.reactor.UserStatusManager;
import com.example.commondto.error.ErrorResponse;
import com.example.shopuserservice.web.util.reactor.Reactor;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.commondto.dto.order.ResponseOrder;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


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

    public UserCommandQueryServiceImpl(UserRepository userRepository,
        UserTransactionRedisRepository userTransactionRedisRepository,
        @Qualifier("taskExecutor") Executor serviceExecutor,
        HikariDataSource hikariDataSource,
        @Qualifier("bcrypt") PasswordEncoder pwe,
        KafkaTemplate<String, Object> kafkaProducerTemplate,
        TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.userTransactionRedisRepository = userTransactionRedisRepository;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
        this.pwe = pwe;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.transactionTemplate = transactionTemplate;
    }


    // 유저 검색
    @Override
    public CompletableFuture<Optional<User>> getUserById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            return userRepository.findById(id);
        });
    }

    @Override
    public CompletableFuture<UserTransactions> updateStatus(UserResponseEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            UserTransactions userTransactions = userTransactionRedisRepository
                .findById(event.getEventId())
                .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

            UserRedisManager.changeUserRegisterStatusByEventResponse(event, userTransactions);

            UserStatusManager.sendUserStatusToClient(userTransactions, event.getUserId());

            return userTransactionRedisRepository.save(userTransactions);   // redis status save
        });
    }

    // 유저 저장
    @Override
    public CompletableFuture<User> createUser(RequestUser request, UUID eventId) {
        return CompletableFuture.supplyAsync(() -> {
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
    public CompletableFuture<UserTransactions> newUserEvent(RequestUser req, UUID eventId,
        UserEvent userEvent) {
        return CompletableFuture.supplyAsync(() -> {
            return transactionTemplate.execute((status) -> {
                log.trace("newUserEvent method is called");

                UserTransactions userTransaction = createUserTransaction(req, eventId);
                // userTransaction event publish to client
                Reactor.emit(req.getUserId(), userTransaction);

                // userTransaction save to redis
                // && Lcoaldatetime for check redis saving time and kafka sending time

                log.trace("UserTransaction save to redis");
                UserTransactions ut = userTransactionRedisRepository.save(userTransaction);
                log.trace("UserTransaction save to redis complete");

                Optional<User> findUser = userRepository.findById(ut.getUserId());
                if (findUser.isEmpty()) {
                    userRepository.save(User.createUser(req, pwe));
                    log.trace("User is saved in DB");
                    ut.setUserStatus(UserStatus.USER_INSERT_SUCCESS);
                } else {
                    log.trace("Already user exist in postgres");
                    ut.setUserStatus(UserStatus.USER_DUPLICATION);
                }

                userTransactionRedisRepository.save(ut);

                log.trace("Now send event to other server");
                sendToKafkaWithKey(
                    KafkaTopic.userReq, // topic
                    userEvent,  // event
                    req.getUserId() // key
                );

                return ut;
            });
        });
    }

    @NotNull
    private UserTransactions createUserTransaction(RequestUser req, UUID eventId) {
        LocalDateTime currentTime = DateFormat.getCurrentTime();
        return UserTransactions.builder()
            .eventId(eventId)
            .createdAt(currentTime)
            .userId(req.getUserId())
            .email(req.getEmail())
            .userName(req.getUserName())
            .userPw(pwe.encode(req.getUserPw()))
            .role(req.getRole())
            .userStatus(UserStatus.USER_INSERT_APPEND)
            .chatStatus(UserStatus.USER_INSERT_APPEND)
            .customerStatus(UserStatus.USER_INSERT_APPEND)
            .build();
    }

    private void queryWithMethods(String userId, String userPw,
        DeferredResult<ResponseEntity<?>> dr, CompletableFuture<?> cf) {
        CompletableFuture.runAsync(() -> {
            try {
                cf.get(); // Blocking
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }, serviceExecutor).thenRunAsync(() -> {
            dr.setResult(ResponseEntity.ok("success"));
        }, serviceExecutor).exceptionally(e -> {
            if (e.getCause().getCause() instanceof CustomException) {
                dr.setResult(ErrorResponse.toResponseEntity(
                    ((CustomException) e.getCause().getCause()).getErrorCode()));
            } else {
                dr.setResult(ResponseEntity.badRequest().body("default bad request response"));
            }
            return null;
        });
    }

    // 유저 삭제
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
        if (findUser.isPresent()) {
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

        User user = userRepository.findById(username).orElseThrow(()->new UsernameNotFoundException(username));
        List<ResponseOrder> orders = null;


        UserDto userDto = new ModelMapper().map(user, UserDto.class);

        userDto.setOrders(orders);
        return CompletableFuture.completedFuture(userDto);
    }

    @Override
    @Transactional
    public CompletableFuture<String> changePassword(String userId, String userPw) {
        User findUser = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("cannot find user"));
        findUser.setUserPw(pwe.encode(userPw));
        return CompletableFuture.completedFuture("success");
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

    private CompletableFuture<?> sendToKafkaWithKey(String topic, Object req, String key) {
        return kafkaProducerTemplate.send(topic, key, req);
    }


}
