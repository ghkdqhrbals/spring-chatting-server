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

@Component
@Slf4j
public class AuthorizationHeaderAdminFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderAdminFilter.Config> {

    @Value("${token.expiration_time}")
    Long expirationTime;

    @Value("${token.secret}")
    String secret;

    public AuthorizationHeaderAdminFilter(){
        super(Config.class);
    }

    public static class Config{
    }

    @Override
    public GatewayFilter apply(AuthorizationHeaderAdminFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = bearerToken.replace("Bearer ","");

            if (!isJwtValidAdmin(jwt)){
                return onError(exchange, "Non valid JWT token", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValidAdmin(String jwt) {
        boolean returnValue = true;
        String subject = null;
        String permissions = null;

        try {
            Claims jwtBody = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody();

            subject = jwtBody.getSubject();
            permissions = (String) jwtBody.get("permissions");

            log.info("subject={}, permissions={}",subject,permissions);
            if (permissions.contains("ROLE_ADMIN")){
                return returnValue;
            }else{
                returnValue = false;
            }
        }catch(Exception e){
            log.info(e.getMessage());
            // validation failed
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()){
            returnValue = false;
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