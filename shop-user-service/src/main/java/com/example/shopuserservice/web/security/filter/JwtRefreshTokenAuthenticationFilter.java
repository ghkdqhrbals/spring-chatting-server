package com.example.shopuserservice.web.security.filter;

import com.example.shopuserservice.web.security.JwtTokenProvider;
import com.example.shopuserservice.web.security.token.UserRedisSession;
import com.example.shopuserservice.web.security.token.UserRedisSessionRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshTokenAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRedisSessionRepository userRedisSessionRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String refreshToken = resolveToken(exchange.getRequest());
        if(refreshToken != null && this.jwtTokenProvider.validateToken(refreshToken, exchange) && isTokenInRedis(refreshToken)){

            Authentication authentication = this.jwtTokenProvider.getAuthentication(refreshToken);

            // if refresh token is valid, create new access token and add it to cookie
            String accessToken = jwtTokenProvider.createToken(authentication);

            exchange.getResponse().addCookie(
                    ResponseCookie.from("accessToken",accessToken)
                            .path("/")
                            .httpOnly(true)
                            .sameSite("Lax")
                            .secure(true)
                            .build()
            );
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        }
        return chain.filter(exchange);
    }

    private String resolveToken(ServerHttpRequest request) {
        HttpCookie refreshToken = request.getCookies().getFirst("refreshToken");
        if(refreshToken == null){
            return null;
        }
        return refreshToken.getValue();
    }

    private Boolean isTokenInRedis(String refreshToken) {
        Optional<UserRedisSession> findUserId = userRedisSessionRepository.findById(refreshToken);
        if(findUserId.isPresent()){
            return true;
        }
        return false;
    }
}
