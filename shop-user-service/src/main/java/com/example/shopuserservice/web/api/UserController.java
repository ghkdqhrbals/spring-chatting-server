package com.example.shopuserservice.web.api;

import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.domain.user.service.UserReadService;
import com.example.shopuserservice.domain.user.service.modules.UserRedisManager;
import com.example.shopuserservice.web.error.CustomException;
import com.example.shopuserservice.web.error.ErrorCode;
import com.example.shopuserservice.web.error.ErrorResponse;
import com.example.shopuserservice.web.security.LoginRequestDto;
import com.example.shopuserservice.web.security.LoginResponseDto;
import com.example.shopuserservice.web.security.LoginService;
import com.example.shopuserservice.web.util.reactor.Reactor;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseUser;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static com.example.shopuserservice.web.security.JwtTokenProvider.getUserIdFromSpringSecurityContext;


@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandQueryService userCommandQueryService;
    private final LoginService loginService;
    private final UserRedisManager userRedisManager;
    private final UserReadService userReadService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    private final Environment env;

    @GetMapping("/")
    public CompletableFuture<String> welcome(ServletRequest request){
        return CompletableFuture.completedFuture("Access auth-controller port "+ String.valueOf(request.getRemotePort()));
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginRequestDto request,
                                        ServerHttpResponse response){
        return loginService.login(request, response).log();
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
    public CompletableFuture<ResponseEntity<ResponseUser>> findUser(){
        String userId = getUserIdFromSpringSecurityContext();
        return userCommandQueryService.getUserDetailsByUserId(userId).thenApply((userDto -> {
            List<UserTransactions> userTransactions = null;
            try {
                userReadService.getRecentUserAddTransaction(userId).get()
                        .forEach(u ->userTransactions.add(u));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(userTransactions);
            ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);
            result.setUserTransaction(userTransactions);
            return ResponseEntity.ok(result);
        }));
    }

    // add user with sse
    @PostMapping(value = "/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> addUser(@RequestBody RequestUser req) throws Exception {
        log.trace("addUser method is called");
        // event id for kafka
        UUID eventId = UUID.randomUUID();

        // add sink for sse
        Reactor.addSink(req.getUserId());
        UserEvent userEvent = new UserEvent(
                eventId,
                UserStatus.USER_INSERT_APPEND,
                req.getUserId()
        );

        // event publishing to kafka and event handling
        userCommandQueryService
                .newUserEvent(req, eventId, userEvent)
                .exceptionally(e -> {
                    if (e.getCause() instanceof CustomException) {
                        Reactor.emitErrorAndComplete(req.getUserId(), e.getCause());
                    } else {
                        Reactor.emitErrorAndComplete(req.getUserId(), new CustomException(ErrorCode.SERVER_ERROR));
                    }
                    return null;
                });
        return Reactor.getSink(req.getUserId()).log();
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
//    @DeleteMapping("/user")
//    public Flux<?> removeUser(WebSession session){
//        // JWT validation 과정애서 session 에 저장한 payload 의 sub:userId 를 가져옴
//        String userId = session.getAttribute("userId");
//
//        // sse 를 위한 Sinks 객체 추가
//        AsyncConfig.sinkMap.put(userId, Sinks.many().multicast().onBackpressureBuffer());
//
//        // 이벤트 ID 및 이벤트
//        UUID eventId = UUID.randomUUID();
//        UserEvent userEvent = new UserEvent(
//                eventId,
//                UserStatus.USER_DELETE,
//                userId
//        );
//
//        // 유저 삭제 이벤트 전송 및 예외 처리
//        userCommandQueryService
//            .deleteUserEvent(eventId, userEvent)
//            .exceptionally(e -> {
//                if (e.getCause() instanceof CustomException) {
//                    CustomException e2 = ((CustomException) e.getCause());
//                    AsyncConfig.sinkMap.get(userId).tryEmitError(new ResponseStatusException(e2.getErrorCode().getHttpStatus(), e2.getErrorCode().getDetail()));
//                } else {
//                    AsyncConfig.sinkMap.get(userId).tryEmitError(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
//                }
//                return null;
//            });
//        return AsyncConfig.sinkMap.get(userId).asFlux().log();
//    }

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
}
