package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.repository.UserRepositoryJDBC;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.kafka.KafkaTopicConst;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;

import static chatting.chat.web.error.ErrorCode.*;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends KafkaTopicConst implements UserService  {
    private final UserRepository userRepository;
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    private final Executor serviceExecutor;

    public UserServiceImpl(UserRepository userRepository,
                           UserRepositoryJDBC userRepositoryJDBC,
                           KafkaTemplate<String, Object> kafkaProducerTemplate,
                           @Qualifier("taskExecutorForService") Executor serviceExecutor) {
        this.userRepository = userRepository;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.serviceExecutor = serviceExecutor;
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
        }, serviceExecutor).thenCompose(u -> {
            return userRepositoryJDBC.saveAll(u); // blocked
        }).exceptionally(e -> {
            if (e.getCause() instanceof CustomException){
                CustomException ex = (CustomException) e.getCause();
                throw ex;
            } else{
                throw new RuntimeException();
            }
        });
    }

    // 로그인
    @Override
    public User login(String userId, String userPw) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            if (findUser.get().getUserPw().equals(userPw)){
                findUser.get().setLoginDate(LocalDate.now());
                return findUser.get();
            }
            throw new CustomException(INVALID_PASSWORD);
        }
        throw new CustomException(CANNOT_FIND_USER);
    }

    // 로그아웃
    @Override
    public void logout(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            findUser.get().setLogoutDate(LocalDate.now());
        }else{
            throw new CustomException(CANNOT_FIND_USER);
        }
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


}
