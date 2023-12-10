package chatting.chat.web.config;

import com.example.commondto.error.AuthorizedException;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${backend.api.gateway}")
    private String backEntry;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .baseUrl(backEntry)
            .filter(addCookiesFilterFunction())
            .filter(handle401And403ThrowAuthorizedException());
    }

    private ExchangeFilterFunction handle401And403ThrowAuthorizedException()
        throws AuthorizedException {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status code: {}", clientResponse.statusCode());
            if (clientResponse.statusCode().value() == 401) {
                log.trace("User is not authenticated");
                String redirectUrl = "/login";
                return Mono.error(new AuthorizedException(ErrorCode.INVALID_CREDENTIAL, redirectUrl));
            } else if (clientResponse.statusCode().value() == 403) {
                log.trace("User is not authorized");
                String redirectUrl = "/login";
                return Mono.error(new AuthorizedException(ErrorCode.FORBIDDEN_USER, redirectUrl));
            } else if (clientResponse.statusCode().value() == 503) {
                // This occured when server is not initialized
                log.trace("Server is not initialized in EUREKA");
                String redirectUrl = "/login";
                return Mono.error(new CustomException(ErrorCode.SERVICE_UNAVAILABLE));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction addCookiesFilterFunction() {

        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("With Cookies: {}", clientRequest.cookies());
            return next.exchange(clientRequest);
        };
    }

}