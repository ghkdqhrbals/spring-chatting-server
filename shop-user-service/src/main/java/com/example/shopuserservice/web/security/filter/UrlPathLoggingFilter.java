package com.example.shopuserservice.web.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class UrlPathLoggingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("METHOD: {}, URL: {}",exchange.getRequest().getMethod(), exchange.getRequest().getURI().toString());
        return chain.filter(exchange);
    }
}
