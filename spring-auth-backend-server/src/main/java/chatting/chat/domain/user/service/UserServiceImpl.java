package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.repository.UserRepositoryJDBC;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestUserChange;
import chatting.chat.web.vo.RequestAddUserVO;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.context.request.async.DeferredResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    private final PasswordEncoder pwe;

    public UserServiceImpl(UserRepository userRepository,
                           UserRepositoryJDBC userRepositoryJDBC,
                           KafkaTemplate<String, Object> kafkaProducerTemplate,
                           @Qualifier("taskExecutorForService") Executor serviceExecutor,
                           HikariDataSource hikariDataSource,
                           PasswordEncoder pwe) {
        this.userRepository = userRepository;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
        this.pwe = pwe;
    }

    private ResponseEntity defaultErrorResponse(){
        return ResponseEntity.badRequest().body("default Error");
    }

    // 유저 검색
    @Override
    @Async
    @Transactional
    public DeferredResult<ResponseEntity<?>> findById(String id, DeferredResult<ResponseEntity<?>> dr) {
        try {
            List<User> users = userRepository.findByUserId(id);
            if (users.size() == 0) {
                dr.setResult(ErrorResponse.toResponseEntity(new CustomException(CANNOT_FIND_USER).getErrorCode()));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
                return dr;
            }
            dr.setResult(ResponseEntity.ok(users.get(0)));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            dr.setErrorResult(defaultErrorResponse());
        }
        return dr;
    }

    // 유저 저장
    @Override
    @Async
    @Transactional
    public DeferredResult<ResponseEntity<?>> save(RequestAddUserVO request, DeferredResult<ResponseEntity<?>> dr) throws CustomException {

        User user = new User(
                request.getUserId(),
                pwe.encode(request.getUserPw()),
                request.getEmail(),
                request.getUserName(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );



        try {
            userRepositoryJDBC.saveAll2(Arrays.asList(user));
            dr.setResult(ResponseEntity.ok("success"));
        } catch (CustomException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            dr.setErrorResult(ErrorResponse.toResponseEntity(e.getErrorCode()));
        } catch ( Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            dr.setErrorResult(defaultErrorResponse());
        }
        return dr;
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
