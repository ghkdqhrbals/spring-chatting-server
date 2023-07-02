package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${token.expiration_time}")
    Long expirationTime;

    @Value("${token.secret}")
    String secret;

    public AuthorizationHeaderFilter(){
        super(Config.class);
    }

    public static class Config{
    }

    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = bearerToken.replace("Bearer ","");

            if (!isJwtValid(exchange, jwt)){
                return onError(exchange, "Non valid JWT token", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(ServerWebExchange exchange, String jwt) {
        boolean returnValue = true;
        String subject = null;
        String userId = null;
        String permissions = null;

        try {
            Claims jwtBody = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody();

            subject = jwtBody.getSubject();
            if (subject == null || subject.isEmpty()){
                returnValue = false;
            }

            permissions = (String) jwtBody.get("permissions");
            userId = (String) jwtBody.get("sub");

            // USER, ADMIN 둘 다 아니라면
            if (!permissions.contains("ROLE_USER") && !permissions.contains("ROLE_ADMIN")){
                returnValue = false;
            // USER, ADMIN 둘 중 하나는 있다면
            }else{
                returnValue = true;
            }

        // 비정상 jwt 이라면
        }catch(Exception e){
            log.info(e.getMessage());
            // validation failed
            returnValue = false;
        }

        if (returnValue){
            if (returnValue){
                String finalUserId = userId;
                exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders -> httpHeaders.set("user_id", finalUserId));
            }
        }

        return returnValue;
    }

    // Mono, Flux = WebFlux 의 반환데이터 타입.
    // 단일 Class값을 반환값으로 예상하면 Mono로 처리가능
    // 다중값, <T> 로 오고 각각 처리할려면 Flux로 하면 됨.
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus unauthorized) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(unauthorized);

        log.error(err);
        return response.setComplete();
    }
}
