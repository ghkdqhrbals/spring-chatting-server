package com.example.shopuserservice.web.controller;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.user.service.UserService;
import com.example.shopuserservice.web.vo.RequestRole;
import com.example.shopuserservice.web.vo.RequestUserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;


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
        CompletableFuture<Mono<List<User>>> set = userService.getAllUser().thenApply(users -> {
            log.info("SET");
            return Mono.just(users);
        });
        return set.get();
    }
}
