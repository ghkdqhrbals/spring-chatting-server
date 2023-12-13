package com.example.shopuserservice.web.api;

import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.domain.user.service.UserReadService;

import com.example.shopuserservice.web.security.LoginService;
import com.example.shopuserservice.web.util.reactor.Reactor;
import com.example.shopuserservice.web.vo.RequestUser;
import com.example.shopuserservice.web.vo.ResponseUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.example.shopuserservice.web.security.JwtTokenProvider.getUserIdFromSpringSecurityContext;


@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandQueryService userCommandQueryService;
    private final UserReadService userReadService;
    private final Environment env;

    @GetMapping("/health-check")
    @Operation(summary = "Check server's status")
    public Mono<String> hello(ServerHttpRequest request) {
        return Mono.just("Server status: " + String.valueOf(request.getRemoteAddress()));
    }


    @GetMapping("/user")
    @Operation(summary = "Get user info based on Cookies that is sent from client")
    public CompletableFuture<ResponseEntity<ResponseUser>> findUser() {
        String userId = getUserIdFromSpringSecurityContext();
        return userCommandQueryService
            .getUserDetailsByUserId(userId).thenApply((userDto -> {
                List<UserTransactions> userTransactions = null;
                try {
                    userReadService.getRecentUserAddTransaction(userId).get()
                        .forEach(u -> userTransactions.add(u));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);
                result.setUserTransaction(userTransactions);
                return ResponseEntity.ok(result);
            }));
    }

    /**
     * Add user with SSE and Kafka
     *
     * @param req {@link RequestUser}
     * @return {@link Flux}
     * @throws Exception
     */
    @PostMapping(value = "/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> addUser(@RequestBody RequestUser req) throws Exception {
        // create event id for kafka
        UUID eventId = UUID.randomUUID();

        // add sink for sse
        Reactor.addSink(req.getUserId());

        UserEvent userEvent = new UserEvent(
            eventId,
            UserStatus.USER_INSERT_APPEND,
            req.getUserId(),
            req.getUserName()
        );

        // event publishing to kafka and event handling
        userCommandQueryService
            .newUserEvent(req, eventId, userEvent)
            .exceptionally(e -> {
                log.trace("exception is occurred: " + e.getMessage());
                if (e.getCause() instanceof CustomException) {
                    Reactor.emitErrorAndComplete(req.getUserId(), e.getCause());
                } else {
                    Reactor.emitErrorAndComplete(req.getUserId(),
                        new CustomException(ErrorCode.SERVER_ERROR));
                }
                return null;
            });
        return Reactor.getSink(req.getUserId());
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
     * Update user's password
     * @param userPw {@link String}
     * @return {@link Boolean}
     */
    @PutMapping("/user")
    public CompletableFuture<ResponseEntity> updateUser(@RequestParam(name = "userPw") String userPw) {
        String userId = getUserIdFromSpringSecurityContext();
        return userCommandQueryService.changePassword(userId, userPw).thenApply((s) -> {
            return ResponseEntity.ok("success");
        }).exceptionally((e) -> {
            return ResponseEntity.badRequest().body("fail");
        }).thenApply(res -> {
            return res;
        });
    }
}
