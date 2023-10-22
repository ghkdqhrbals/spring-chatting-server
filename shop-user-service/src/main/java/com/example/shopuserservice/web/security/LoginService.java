package com.example.shopuserservice.web.security;


import com.example.shopuserservice.domain.user.redisrepository.UserRefreshTokenRedisRepository;
import com.example.shopuserservice.web.security.token.CookieUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserRefreshTokenRedisRepository userRefreshTokenRedisRepository;



    public Mono<String> login(LoginRequestDto loginRequestDto, ServerHttpResponse response) {
        log.trace("login service access");

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
                            .sameSite("Lax")
                            .secure(true)
                            .build());
                    response.addCookie(ResponseCookie.from("refreshToken", refreshToken)
                            .httpOnly(true)
                            .path("/")
                            .sameSite("Lax")
                            .secure(true)
                            .build());
                    return "login success";
                });
    }

    public Mono<String> logout(HttpServletResponse response) {
        log.trace("Delete refresh token in redis");
        userRefreshTokenRedisRepository.deleteById(SecurityContextHolder.getContext().getAuthentication().getName());
        CookieUtil.removeCookie(response, "accessToken");
        CookieUtil.removeCookie(response, "refreshToken");
        return Mono.just("logout success");
    }
}