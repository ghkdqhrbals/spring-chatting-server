package com.example.shopuserservice.web.security;

import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.web.server.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * JWT 토큰이 http request header에 있는지 확인하기 위한 필터
 * 토근이 있을 경우, 유효성 체크 후, 토큰을 이용하여 인증 정보를 만든다.
 */

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpCookie tokenCookie = exchange.getRequest().getCookies().getFirst("accessToken");
        if (tokenCookie == null){
            return chain.filter(exchange);
        }

        String token = tokenCookie.getValue();
        if(StringUtils.hasText(token) && this.jwtTokenProvider.validateToken(token, exchange)) {
            Authentication authentication = this.jwtTokenProvider.getAuthentication(token);
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }
}
