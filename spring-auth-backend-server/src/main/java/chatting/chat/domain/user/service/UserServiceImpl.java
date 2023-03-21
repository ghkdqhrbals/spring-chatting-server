package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.repository.UserRepositoryJDBC;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestUserChange;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.async.DeferredResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;

import static chatting.chat.web.error.ErrorCode.*;

@Slf4j
@Service
public class UserServiceImpl extends KafkaTopicConst implements UserService  {
    private final UserRepository userRepository;
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    private final Executor serviceExecutor;

    private final HikariDataSource hikariDataSource;

    public UserServiceImpl(UserRepository userRepository,
                           UserRepositoryJDBC userRepositoryJDBC,
                           KafkaTemplate<String, Object> kafkaProducerTemplate,
                           @Qualifier("taskExecutorForService") Executor serviceExecutor, HikariDataSource hikariDataSource) {
        this.userRepository = userRepository;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
    }

    // 유저 검색
    @Override
    public User findById(String id) {
        Optional<User> findId = userRepository.findById(id);
        if (!findId.isPresent()){
            throw new CustomException(CANNOT_FIND_USER);
        }
        return findId.get();
    }


    // 유저 저장
    @Override
    public CompletableFuture<?> save(User user) throws CustomException {
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        return CompletableFuture.supplyAsync(()->{
            return Arrays.asList(user);
        }, serviceExecutor).thenAccept(u -> {
            try {
                userRepositoryJDBC.saveAll(u).get(); // blocked
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(e -> {
            if (e.getCause() instanceof CustomException){
                CustomException ex = (CustomException) e.getCause();
                throw ex;
            } else{
                throw new RuntimeException();
            }
        });
    }

//    public DeferredResult<Response<?>> saveUserHandler(DeferredResult<ResponseEntity<?>> dr, User user) {
//        CompletableFuture.runAsync(() -> {
//        }).thenCompose(s -> {
//            log.info("SET dr1");
//            return userService.save(user);
//        }).thenAcceptAsync(s1 -> {
//            sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(user.getUserId(), user.getUserName(), "", "INSERT"), user.getUserId());
//            log.info("SET dr2");
//            dr.setResult(ResponseEntity.ok("success"));
//        }).exceptionally(e -> {
//            if (e.getCause() instanceof CustomException) {
//                dr.setResult(ErrorResponse.toResponseEntity(((CustomException) e.getCause()).getErrorCode()));
//            } else {
//                dr.setResult(ResponseEntity.badRequest().body("default bad request response"));
//            }
//            return null;
//        });
//
//    }

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
    public void removeUser(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            userRepository.delete(findUser.get());
            return;
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

    private void printHikariCPInfo() {
        log.info(String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        ));
    }


}
