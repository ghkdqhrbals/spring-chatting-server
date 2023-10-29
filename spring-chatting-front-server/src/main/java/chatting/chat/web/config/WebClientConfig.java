package chatting.chat.web.config;

import chatting.chat.web.error.AuthorizedException;
import chatting.chat.web.error.ErrorCode;
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

    private ExchangeFilterFunction handle401And403ThrowAuthorizedException() throws AuthorizedException {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status code: {}", clientResponse.statusCode());
            if (clientResponse.statusCode().value()==401) {
                String redirectUrl = "/login";
                return Mono.error(new AuthorizedException(ErrorCode.INVALID_CREDENTIAL,redirectUrl));
            }else if (clientResponse.statusCode().value()==403) {
                String redirectUrl = "/login";
                return Mono.error(new AuthorizedException(ErrorCode.FORBIDDEN_USER,redirectUrl));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction addCookiesFilterFunction() {

        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("With Cookies: {}",clientRequest.cookies());
            return next.exchange(clientRequest);
        };
    }

}