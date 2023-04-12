package com.example.shopuserservice.web.controller;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.domain.user.service.UserReadService;
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
    public Mono<String> accessSuccess() {
        return Mono.just("ADMIN access").log();
    }

    /**
     * 모든 유저정보 가져오기
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/users")
    public Mono<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        CompletableFuture<Mono<List<User>>> set = userCommandQueryService.getAllUser().thenApply(users -> {
            log.info("SET");
            return Mono.just(users);
        });
        return set.get();
    }

    @GetMapping("/users/tx")
    public CompletableFuture<List<UserTransaction>> getAllUserAddTransaction(){
        return userReadService.getAllUserAddTransaction();
    }
}
