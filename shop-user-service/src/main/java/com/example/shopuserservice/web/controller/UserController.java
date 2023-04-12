package com.example.shopuserservice.web.controller;

import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.events.topic.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.security.LoginRequestDto;
import com.example.shopuserservice.web.security.LoginResponseDto;
import com.example.shopuserservice.web.security.LoginService;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseAddUser;
import com.example.shopuserservice.web.vo.ResponseUser;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;


@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandQueryService userCommandQueryService;
    private final LoginService loginService;
    private final HikariDataSource hikariDataSource;

    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
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
        log.info("start with key={}",env.getProperty("token.secret"));
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

        return userCommandQueryService.getUserDetailsByUserId(userId).thenApply((userDto -> {
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
        userCommandQueryService.logout(userId, userPw, dr);
        return Mono.just((ResponseEntity) dr.getResult());
    }

    /**
     * -------------- CREATE METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public CompletableFuture<ResponseEntity<ResponseAddUser>> addUser(@RequestBody RequestUser req) throws InterruptedException {
        // saga choreograhpy tx 관리 id;
        UUID eventId = UUID.randomUUID();

        return userCommandQueryService.createUser(req, eventId.toString()).thenApply((user)->{
            sendToKafkaWithKey(KafkaTopic.user_add_req,
                    new UserEvent(
                            eventId,
                            UserStatus.USER_INSERT,
                            new RequestUserChangeDto(req.getUserId(),req.getUserName(),req.getEmail())
                    ), req.getUserId()).thenRun(()->{
                        log.info("send kafka message");
            });
            ResponseAddUser res = new ModelMapper().map(user, ResponseAddUser.class);
            return ResponseEntity.ok(res);
        }).exceptionally(e->{
            if (e.getCause() instanceof CustomException){
                CustomException e2 = ((CustomException) e.getCause());
                throw new ResponseStatusException(e2.getErrorCode().getHttpStatus(), e2.getErrorCode().getDetail());
            }else{
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
            }
        });
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
        userCommandQueryService.updateUser(user);
        return ResponseEntity.ok("success");
    }

    /**
     * -------------- Utils --------------
     */

    private CompletableFuture<?> sendToKafkaWithKey(String topic,Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }

}
