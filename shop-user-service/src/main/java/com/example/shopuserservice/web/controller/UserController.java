package com.example.shopuserservice.web.controller;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.user.service.UserService;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.security.LoginRequestDto;
import com.example.shopuserservice.web.security.LoginResponseDto;
import com.example.shopuserservice.web.security.LoginService;
import com.example.shopuserservice.web.vo.RequestLogin;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseUser;
import com.zaxxer.hikari.HikariDataSource;
import feign.Response;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import static com.example.shopuserservice.web.error.ErrorCode.CANNOT_FIND_USER;


@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginService loginService;
    private final HikariDataSource hikariDataSource;
    private final Environment env;

    private ResponseEntity defaultErrorResponse(){
        return ResponseEntity.badRequest().body("default Error");
    }

    @GetMapping("/")
    public CompletableFuture welcome(ServletRequest request){
        return CompletableFuture.completedFuture("Access auth-controller port "+ String.valueOf(request.getRemotePort()));
    }


    @GetMapping("/d")
    public Mono<String> welcome2(){
        return Mono.just("welcome2");
    }

    @GetMapping("/b")
    public Mono<String> greet(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> String.format("Hello, %s", name));
    }

    @PostMapping("/login")
    public Mono<LoginResponseDto> login(@RequestBody LoginRequestDto request,
                                        ServerHttpResponse response){
        Mono<LoginResponseDto> login = loginService.login(request, response).log();
        return login;
    }

    @GetMapping("/health-check")
    public Mono<String> hello(ServerHttpRequest request){
        return Mono.just("Access auth-controller port "+
                String.valueOf(request.getRemoteAddress().getPort()+","+
                        env.getProperty("token.expiration_time")+","+
                        env.getProperty("token.secret")));
    }
    /**
     * -------------- READ METHODS --------------
     */
    // 유저 조회
    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<ResponseUser>> findUser(@PathVariable("userId") String userId){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();

        return userService.getUserDetailsByUserId(userId).thenApply((userDto -> {
            return ResponseEntity.ok(new ModelMapper().map(userDto, ResponseUser.class));
        })).exceptionally(e->{
            log.info(e.getCause().getClass().getName());

            if( e.getCause() instanceof UsernameNotFoundException){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User name not found");
//                dr.setErrorResult(ErrorResponse.toResponseEntity(new CustomException(CANNOT_FIND_USER).getErrorCode()));
            }

            if (e.getCause() instanceof ResponseStatusException){
                log.info("ResponseStatusException");
                ResponseStatusException e2 = (ResponseStatusException) e.getCause();
                throw e2;
            }

            return defaultErrorResponse();
        });
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

    @GetMapping(value = "/f")
    public CompletableFuture<String> examples() {
        return CompletableFuture.completedFuture("hi~");
    }

    // 로그아웃
    @GetMapping("/logout")
    public Mono<ResponseEntity<?>> logout(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        userService.logout(userId, userPw, dr);
        return Mono.just((ResponseEntity) dr.getResult());
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
