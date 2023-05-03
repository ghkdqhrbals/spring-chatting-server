package com.example.shopuserservice.web.controller;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.config.AsyncConfig;
import com.example.shopuserservice.domain.data.UserTransaction;
import com.example.shopuserservice.domain.user.repository.UserTransactionRedisRepository;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.domain.user.service.UserReadService;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
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
    private final UserReadService userReadService;
    private final UserTransactionRedisRepository userTransactionRedisRepository;
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
    @GetMapping("/user")
    public CompletableFuture<ResponseEntity<ResponseUser>> findUser(WebSession session){
        String userId = session.getAttribute("userId");
        return userCommandQueryService.getUserDetailsByUserId(userId).thenApply((userDto -> {
            List<UserTransaction> userTransactions = null;
            try {
                userTransactions = userReadService.getRecentUserAddTransaction(userId).get();
                System.out.println(userTransactions);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(userTransactions);
            ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);
            result.setUserTransaction(userTransactions);
            return ResponseEntity.ok(result);
        })).exceptionally(e->{
            log.info(e.getCause().getClass().getName());

            if( e.getCause() instanceof UsernameNotFoundException){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User name not found");
            }

            if (e.getCause() instanceof ResponseStatusException){
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
    // 유저 저장(deprecated)
//    @PostMapping("/user")
//    public CompletableFuture<ResponseEntity<ResponseAddUser>> addUser(@RequestBody RequestUser req) throws InterruptedException {
//        // saga choreograhpy tx 관리 id;
//        UUID eventId = UUID.randomUUID();
//        UserEvent userEvent = new UserEvent(
//                eventId,
//                UserStatus.USER_INSERT,
//                req.getUserId()
//        );
//        // 이벤트 Publishing (만약 MQ가 닫혀있으면 exception)
//        return userCommandQueryService.newUserEvent(req, eventId, userEvent).thenCompose((c)->{
//            // 사용자 생성 -> 이벤트에 상관없이 루트 사용자 생성
//            return userCommandQueryService.createUser(req, eventId);
//        }).thenApply((user)->{
//            ResponseAddUser res = new ModelMapper().map(user, ResponseAddUser.class);
//            return ResponseEntity.ok(res);
//        }).exceptionally(e->{
//            if (e.getCause() instanceof CustomException){
//                CustomException e2 = ((CustomException) e.getCause());
//                throw new ResponseStatusException(e2.getErrorCode().getHttpStatus(), e2.getErrorCode().getDetail());
//            }else{
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
//            }
//        });
//    }

    // 유저 저장 Server-Sent Event
    // produces = MediaType.TEXT_EVENT_STREAM_VALUE
    @PostMapping(value = "/user")
    public Flux<?> addUser2(@RequestBody RequestUser req) throws InterruptedException {
        // saga choreograhpy tx 관리 id;
        UUID eventId = UUID.randomUUID();

        AsyncConfig.sinkMap.put(req.getUserId(), Sinks.many().multicast().onBackpressureBuffer());

        UserEvent userEvent = new UserEvent(
                eventId,
                UserStatus.USER_INSERT,
                req.getUserId()
        );


        // 이벤트 Publishing (만약 MQ가 닫혀있으면 exception)
        userCommandQueryService
                .newUserEvent2(req, eventId, userEvent)
                .exceptionally(e -> {
                    if (e.getCause() instanceof CustomException) {
                        CustomException e2 = ((CustomException) e.getCause());
                        AsyncConfig.sinkMap.get(req.getUserId()).tryEmitError(new ResponseStatusException(e2.getErrorCode().getHttpStatus(), e2.getErrorCode().getDetail()));
                    } else {
                        AsyncConfig.sinkMap.get(req.getUserId()).tryEmitError(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                    }
                    return null;
                });
        return AsyncConfig.sinkMap.get(req.getUserId()).asFlux().log();
    }

    /**
     * -------------- DELETE METHODS --------------
     */
    // 유저 삭제(deprecated)
//    @DeleteMapping("/user")
//    public CompletableFuture<ResponseEntity<String>> removeUser(WebSession session){
//        // JWT validation 과정 중, session 에 저장한 payload 의 sub:userId 를 가져옴
//        String userId = session.getAttribute("userId");
//        // 이벤트 ID 및 이벤트
//        UUID eventId = UUID.randomUUID();
//        UserEvent userEvent = new UserEvent(
//                eventId,
//                UserStatus.USER_DELETE,
//                userId
//        );
//        // 이번에는 먼저 유저 삭제 후, 토픽에 전송
//        return userCommandQueryService.removeUser(eventId, userId).thenApply(b->{
//            if (b){
//                sendToKafkaWithKey(KafkaTopic.userReq,
//                        userEvent,
//                        userId).thenRun(()->{
//                    log.debug("send kafka message");
//                });
//            }
//            return ResponseEntity.ok().body("delete method is successfully running");
//        }).exceptionally(e->{
//            return ResponseEntity.internalServerError().body("delete method error is occurred");
//        });
//    }


    // 유저 삭제
    @DeleteMapping("/user")
    public Flux<?> removeUser(WebSession session){
        // JWT validation 과정애서 session 에 저장한 payload 의 sub:userId 를 가져옴
        String userId = session.getAttribute("userId");

        // sse 를 위한 Sinks 객체 추가
        AsyncConfig.sinkMap.put(userId, Sinks.many().multicast().onBackpressureBuffer());

        // 이벤트 ID 및 이벤트
        UUID eventId = UUID.randomUUID();
        UserEvent userEvent = new UserEvent(
                eventId,
                UserStatus.USER_DELETE,
                userId
        );

        // 유저 삭제 이벤트 전송 및 예외 처리
        userCommandQueryService
            .deleteUserEvent(eventId, userEvent)
            .exceptionally(e -> {
                if (e.getCause() instanceof CustomException) {
                    CustomException e2 = ((CustomException) e.getCause());
                    AsyncConfig.sinkMap.get(userId).tryEmitError(new ResponseStatusException(e2.getErrorCode().getHttpStatus(), e2.getErrorCode().getDetail()));
                } else {
                    AsyncConfig.sinkMap.get(userId).tryEmitError(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                }
                return null;
            });
        return AsyncConfig.sinkMap.get(userId).asFlux().log();
    }

    /**
     * -------------- UPDATE METHODS --------------
     */
    // 유저 업데이트
    @PutMapping("/user")
    public CompletableFuture<ResponseEntity> updateUser(WebSession session, @RequestParam(name = "userPw") String userPw) {
        String userId = session.getAttribute("userId");
        return userCommandQueryService.changePassword(userId, userPw).thenApply((s)->{
            return ResponseEntity.ok("success");
        }).exceptionally((e)->{
            return ResponseEntity.badRequest().body("fail");
        }).thenApply(res->{
            return res;
        });
    }

    /**
     * -------------- Utils --------------
     */

    private CompletableFuture<?> sendToKafkaWithKey(String topic,Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }

}
