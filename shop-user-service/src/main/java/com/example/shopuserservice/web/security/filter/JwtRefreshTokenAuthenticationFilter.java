package com.example.shopuserservice.web.security.filter;

import com.example.shopuserservice.web.security.JwtTokenProvider;
import com.example.shopuserservice.web.security.token.TokenUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

// for refresh token
public class JwtRefreshTokenAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtRefreshTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String refreshToken = TokenUtil.getRefreshToken(exchange.getRequest());
        if (refreshToken != null) {
            return chain.filter(exchange);
        }

        if(this.jwtTokenProvider.validateToken(refreshToken, exchange)) {
            // check if refreshToken is in redis
            if(!this.jwtTokenProvider.isRefreshTokenInRedis(refreshToken)) {
                return chain.filter(exchange);
            }

            // get Authentication from refreshToken
            Authentication authentication = this.jwtTokenProvider.getAuthentication(refreshToken);

            // refresh accessToken using refreshToken
            String accessToken = jwtTokenProvider.createToken(authentication);
            exchange.getResponse().addCookie(ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .path("/")
                    .build());

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }
}
