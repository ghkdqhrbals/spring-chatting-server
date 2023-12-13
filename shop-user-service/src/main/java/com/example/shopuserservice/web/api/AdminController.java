package com.example.shopuserservice.web.api;

import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.domain.user.service.UserReadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserCommandQueryService userCommandQueryService;
    private final UserReadService userReadService;


    @GetMapping("/")
    @Operation(summary = "Test admin access")
    public Mono<String> accessSuccess() {
        return Mono.just("ADMIN access").log();
    }

    /**
     * getAllUsers method is used to get all users.
     *
     * @return Mono<List < User>>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public Mono<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        CompletableFuture<Mono<List<User>>> set = userCommandQueryService.getAllUser().thenApply(users -> {
            return Mono.just(users);
        });
        return set.get();
    }

    @GetMapping("/users/tx")
    @Operation(summary = "Get all user transactions")
    public CompletableFuture<Iterable<UserTransactions>> getAllUserAddTransaction(){
        return userReadService.getAllUserAddTransaction();
    }
}
