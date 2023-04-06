package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.client.OrderServiceClient;
import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC;
import com.example.shopuserservice.web.dto.UserDto;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseOrder;
import com.zaxxer.hikari.HikariDataSource;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.example.shopuserservice.web.error.ErrorCode.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepository;
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final Executor serviceExecutor;
    private final HikariDataSource hikariDataSource;
    private final PasswordEncoder pwe;
    private final OrderServiceClient orderServiceClient;

    public UserServiceImpl(UserRepository userRepository,
                           UserRepositoryJDBC userRepositoryJDBC,
                           @Qualifier("taskExecutorForService") Executor serviceExecutor,
                           HikariDataSource hikariDataSource,
                           @Qualifier("bcrypt") PasswordEncoder pwe,
                           OrderServiceClient orderServiceClient) {
        this.userRepository = userRepository;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
        this.pwe = pwe;
        this.orderServiceClient = orderServiceClient;
    }



    // 유저 검색
    @Override
    @Async
    @Transactional
    public CompletableFuture<List<User>> getUserById(String id) {
        List<User> users = new ArrayList<User>();

        try {
            users = userRepository.findByUserId(id);
            if (users.size() == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
        }

        return CompletableFuture.completedFuture(users);
    }

    // 유저 저장
    @Override
    @Async
    @Transactional
    public CompletableFuture<User> createUser(RequestUser request) {

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
        } catch (CustomException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            return CompletableFuture.failedFuture(e);
        } catch ( Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 롤백
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(user);
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



    @Override
    @Async
    @Transactional
    public CompletableFuture<UserDto> getUserDetailsByUserId(String username) {
        Optional<User> user = userRepository.findById(username);
        List<ResponseOrder> orders = null;

        if (!user.isPresent()){
            throw new UsernameNotFoundException(username);
        }
        UserDto userDto = new ModelMapper().map(user, UserDto.class);

        try{
            orders = orderServiceClient.getOrders(username);
        }catch (FeignException e){
            log.info(e.getMessage());
        }

        userDto.setOrders(orders);

        return CompletableFuture.completedFuture(userDto);
    }

    private void printHikariCPInfo() {
        log.info(String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        ));
    }


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findById(username);
//        if (!user.isPresent()){
//            throw new UsernameNotFoundException(username);
//        }
//        User findUser = user.get();
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                .username(findUser.getUserId())
//                .password(findUser.getUserPw())
//                .roles("USER")
//                .accountExpired(false)
//                .accountLocked(false)
//                .build();
//
//        return userDetails;
//    }


    public Mono<UserDetails> findByUsername(String username) {
        Optional<User> user = userRepository.findById(username);
        if (!user.isPresent()){
            throw new UsernameNotFoundException(username);
        }
        User findUser = user.get();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(findUser.getUserId())
                .password(findUser.getUserPw())
                .roles("USER")
                .accountExpired(false)
                .accountLocked(false)
                .build();
        return Mono.just(userDetails);
    }
}
