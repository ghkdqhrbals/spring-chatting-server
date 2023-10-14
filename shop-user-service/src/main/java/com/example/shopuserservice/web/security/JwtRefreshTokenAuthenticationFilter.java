package com.example.shopuserservice.web.security;

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

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshTokenAuthenticationFilter implements WebFilter {
    public static final String HEADER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String refreshToken = resolveToken(exchange.getRequest());
        if(StringUtils.hasText(refreshToken) && this.jwtTokenProvider.validateToken(refreshToken, exchange)) {
            Authentication authentication = this.jwtTokenProvider.getAuthentication(refreshToken);

            // if refresh token is valid, create new access token and add it to cookie
            String accessToken = jwtTokenProvider.createToken(authentication);

            exchange.getResponse().addCookie(
                    ResponseCookie.from("accessToken",accessToken)
                            .path("/")
                            .httpOnly(true)
                            .build()
            );

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }

    // Get JWT string from bearer-token in header
    private String resolveToken(ServerHttpRequest request) {
        return request.getCookies().getFirst("refreshToken").getValue();
    }
}
