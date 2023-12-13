package com.example.shopuserservice.web.api;

import com.example.shopuserservice.web.security.LoginRequestDto;
import com.example.shopuserservice.web.security.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "Login for user")
    public Mono<String> login(@RequestBody LoginRequestDto request, ServerHttpResponse response){
        return loginService.login(request, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout for user")
    public Mono<String> logout(HttpServletResponse response){
        return loginService.logout(response);
    }

}
