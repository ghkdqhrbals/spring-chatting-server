package com.example.shopuserservice.domain.user.service;

import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.events.ServiceNames;
import com.example.commondto.events.topic.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.client.OrderServiceClient;
import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC;
import com.example.shopuserservice.domain.user.repository.UserTransactionRepository;
import com.example.shopuserservice.web.dto.UserDto;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseOrder;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.context.request.async.DeferredResult;

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
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final UserTransactionRepository userTransactionRepository;

    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    private final Executor serviceExecutor;
    private final HikariDataSource hikariDataSource;
    private final PasswordEncoder pwe;
    private final OrderServiceClient orderServiceClient;

    public UserCommandQueryServiceImpl(UserRepository userRepository,
                                       UserRepositoryJDBC userRepositoryJDBC,
                                       @Qualifier("taskExecutorForService") Executor serviceExecutor,
                                       HikariDataSource hikariDataSource,
                                       @Qualifier("bcrypt") PasswordEncoder pwe,
                                       OrderServiceClient orderServiceClient,
                                       UserTransactionRepository userTransactionRepository, KafkaTemplate<String, Object> kafkaProducerTemplate) {
        this.userRepository = userRepository;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
        this.pwe = pwe;
        this.orderServiceClient = orderServiceClient;
        this.userTransactionRepository = userTransactionRepository;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
    }



    // 유저 검색
    @Override
    @Async
    @Transactional
    public CompletableFuture<Optional<User>> getUserById(String id) {
        Optional<User> user = Optional.of(new User());
        try {
            user = userRepository.findById(id);
            if (user.isEmpty()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
        }
        return CompletableFuture.completedFuture(user);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<UserTransaction> updateStatus(UserResponseEvent event) {

        Optional<UserTransaction> tx = userTransactionRepository.findByEventId(event.getEventId().toString());
        if (tx.isPresent()){
            UserTransaction ut = tx.get();
            switch (event.getServiceName()){
                case ServiceNames.chat -> {
                    ut.setChatStatus(event.getUserResponseStatus());
                }
                case ServiceNames.customer -> {
                    ut.setCustomerStatus(event.getUserResponseStatus());
                }
            }
            String chatStatus = ut.getChatStatus();
            String customerStatus = ut.getCustomerStatus();
            String userStatus = ut.getUserStatus();
            // 둘 다 SUCCESS 일 경우,
            if (chatStatus.equals(UserResponseStatus.USER_SUCCES.name())
                    && customerStatus.equals(UserResponseStatus.USER_SUCCES.name())){
                // 유저 Status 에 따라 완료/미완료
                if (userStatus.equals(UserStatus.USER_INSERT.name())){
                    ut.setUserStatus(UserStatus.USER_INSERT_COMPLETE.name());
                } else if (userStatus.equals(UserStatus.USER_DELETE.name())) {
                    ut.setUserStatus(UserStatus.USER_DELETE_COMPLETE.name());
                }
            // 둘 중 FAIL 이 있을 경우
            } else if (chatStatus.equals(UserResponseStatus.USER_FAIL.name())
                    || customerStatus.equals(UserResponseStatus.USER_FAIL.name())) {
                // 실패 시, FAIL 처리
                ut.setUserStatus(UserStatus.USER_INSERT_FAIL.name());
            }
        }else{
            return CompletableFuture.failedFuture(new RuntimeException());
        }
        return CompletableFuture.completedFuture(tx.get());
    }

    // 유저 저장
    @Override
    @Async
    @Transactional
    public CompletableFuture<User> createUser(RequestUser request, UUID eventId) {

        User user = new User(
                request.getUserId(),
                pwe.encode(request.getUserPw()),
                request.getEmail(),
                request.getUserName(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                request.getRole()
        );

        try {
            userRepositoryJDBC.saveAll2(Arrays.asList(user));
            // saga Choreography 로 Transaction 관리
        } catch (CustomException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            return CompletableFuture.failedFuture(e);
        } catch ( Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(user);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<UserTransaction> newUserEvent(RequestUser req, UUID eventId, UserEvent userEvent) {

        UserTransaction userTransaction = new UserTransaction(
                eventId,
                UserStatus.USER_INSERT,
                UserResponseStatus.USER_APPEND,
                UserResponseStatus.USER_APPEND,
                req.getUserId(),
                LocalDateTime.now());
        try {
            // 이벤트 Transaction 저장
            userTransactionRepository.save(userTransaction);

            // 이벤트 Publishing key:userId = Partitioning
            sendToKafkaWithKey(
                    KafkaTopic.user_req,
                    userEvent,
                    req.getUserId()
            ).thenRun(()->{
                log.info("send kafka message successfully");
            });
        }catch(Exception e){
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(userTransaction);
    }

    private CompletableFuture<?> sendToKafkaWithKey(String topic,Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }

    // 로그인
    @Override
    public DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr) {
        printHikariCPInfo();
        queryWithMethods(userId, userPw, dr, userRepositoryJDBC.login(userId, userPw)); // blocking
        return dr;
    }

    // 로그인
    @Override
    public DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr) {
        printHikariCPInfo();
        queryWithMethods(userId, userPw, dr, userRepositoryJDBC.logout(userId, userPw));
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


    // 유저 삭제
    @Override
    @Transactional
    @Async
    public CompletableFuture<Boolean> removeUser(UUID eventId, String userId) {

        // tx 저장
        userTransactionRepository.save(
                new UserTransaction(
                        eventId,
                        UserStatus.USER_DELETE,
                        UserResponseStatus.USER_APPEND,
                        UserResponseStatus.USER_APPEND,
                        userId,
                        LocalDateTime.now())
        );

        Optional<User> findUser = userRepository.findById(userId);

        if (findUser.isPresent()){
            userRepository.delete(findUser.get());
            return CompletableFuture.completedFuture(true);
        }

        throw new CustomException(CANNOT_FIND_USER);
    }

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
