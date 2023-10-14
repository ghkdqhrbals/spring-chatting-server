package com.example.shopuserservice.web.security;


import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtTokenProvider jwtTokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;


    public Mono<String> login(LoginRequestDto loginRequestDto, ServerHttpResponse response) {

        if (loginRequestDto.getPassword() == null || loginRequestDto.getUsername() == null)
            return Mono.error(new ServerWebInputException("User Input Invalidation"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                loginRequestDto.getPassword());

        return authenticationManager.authenticate(authentication)
                .map(auth -> {
                    // create Tokens
                    String refreshToken = jwtTokenProvider.createRefreshToken(auth);
                    String accessToken = jwtTokenProvider.createToken(auth);

                    response.addCookie(ResponseCookie.from("accessToken", accessToken)
                            .httpOnly(true)
                            .path("/")
                            .build());
                    response.addCookie(ResponseCookie.from("refreshToken", refreshToken)
                            .httpOnly(true)
                            .path("/")
                            .build());
                    return "login success";
                });
    }
}