package com.example.shopuserservice.web.security;

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
    public static final String HEADER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest());
        if(StringUtils.hasText(token) && this.jwtTokenProvider.validateToken(token, exchange)) {
            Authentication authentication = this.jwtTokenProvider.getAuthentication(token);
//            authentication.getAuthorities().forEach(a->{
//                log.info("JWT 토큰으로 부터 얻는 Authorities={}",a.getAuthority());
//            });
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }

    // Header에서 JWT 토큰을 Bear 프리픽스 떼서 가져옵니다
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
