package com.example.shopuserservice.web.security.filter;

import com.example.shopuserservice.web.security.JwtTokenProvider;
import com.example.shopuserservice.web.security.token.TokenUtil;
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


// for access token
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String accessToken = TokenUtil.getAccessToken(exchange.getRequest());
        if (accessToken != null) {
            return chain.filter(exchange);
        }

        if(this.jwtTokenProvider.validateToken(accessToken, exchange)) {
            Authentication authentication = this.jwtTokenProvider.getAuthentication(accessToken);
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }
}
