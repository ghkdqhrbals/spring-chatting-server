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

        // 클라이언트에게
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
