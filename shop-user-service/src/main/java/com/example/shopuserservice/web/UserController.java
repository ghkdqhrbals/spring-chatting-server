package com.example.shopuserservice.web;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.user.service.UserService;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.vo.RequestLogin;
import com.example.shopuserservice.web.vo.RequestUser;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static com.example.shopuserservice.web.error.ErrorCode.CANNOT_FIND_USER;


@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HikariDataSource hikariDataSource;

    private ResponseEntity defaultErrorResponse(){
        return ResponseEntity.badRequest().body("default Error");
    }


    @GetMapping("/")
    public String welcome(ServletRequest request){
        return "Access auth-controller port "+ String.valueOf(request.getRemotePort());
    }

    @GetMapping("/health-check")
    public String hello(ServletRequest request){
        return "Access auth-controller port "+ String.valueOf(request.getRemotePort());
    }
    /**
     * -------------- READ METHODS --------------
     */
    // 유저 조회
    @GetMapping("/user/{userId}")
    public DeferredResult<ResponseEntity<?>> findUser(@PathVariable("userId") String userId){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        userService.getUserById(userId).thenAccept((users)->{
            if (users.size()<1) {
                dr.setResult(ErrorResponse.toResponseEntity(new CustomException(CANNOT_FIND_USER).getErrorCode()));
            }
            dr.setResult(ResponseEntity.ok(users.get(0)));
        }).exceptionally(e->{
            dr.setErrorResult(defaultErrorResponse());
            return null;
        });
        return dr;
    }

    // deferredResult examples
    @GetMapping(value = "/deferredResult")
    public DeferredResult<String> useDeferredResult() {
        DeferredResult<String> dr = new DeferredResult<>();
        dr.onCompletion(() -> log.info("onCompletion"));
        ForkJoinPool.commonPool().submit(() -> {
            dr.setResult("Results Here");
            log.info("Results set");
        });
        return dr;
    }

//    @GetMapping("/login")
//    public DeferredResult<ResponseEntity<?>> login(@RequestBody RequestLogin login){
//        printHikariCPInfo();
//        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
//        return userService.login(login.getUserId(), login.getUserPw(), dr);
//    }

    // 로그아웃
    @GetMapping("/logout")
    public DeferredResult<ResponseEntity<?>> logout(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        return userService.logout(userId, userPw, dr);
    }

    /**
     * -------------- CREATE METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public DeferredResult<ResponseEntity<?>> addUser(@RequestBody RequestUser req) throws InterruptedException {
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        userService.createUser(req).thenAccept((user)->{
            dr.setResult(ResponseEntity.ok(user));
        }).exceptionally(e->{
            if (e instanceof CustomException){
                dr.setResult(ErrorResponse.toResponseEntity(((CustomException) e).getErrorCode()));
            }else{
                dr.setErrorResult(defaultErrorResponse());
            }
            return null;
        });
        return dr;
    }

    /**
     * -------------- DELETE METHODS --------------
     */
    // 유저 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable("userId") String userId){
//        User user = userService.findById(userId);
//        userService.removeUser(userId);
//
//        // 같은 파티션에 삽입하여 메세지 전송 순서 보장
//        sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(user.getUserId(),user.getUserName(),"","DELETE"), user.getUserId());

        return ResponseEntity.ok("success");
    }

    /**
     * -------------- UPDATE METHODS --------------
     */
    // 유저 업데이트
    @PutMapping("/user")
    public ResponseEntity<?> removeUser(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.ok("success");
    }

    /**
     * -------------- Utils --------------
     */

    <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
        CompletableFuture<T> cf = new CompletableFuture<T>();
        lf.addCallback(s-> cf.complete(s), e-> cf.completeExceptionally(e));
        return cf;
    }
    private String printHikariCPInfo() {
        return String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        );
    }

}
