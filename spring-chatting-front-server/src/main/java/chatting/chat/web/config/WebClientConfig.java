package chatting.chat.web.config;

import chatting.chat.web.error.AuthorizedException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.login.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
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
                .filter(handle4xxErrors());
    }

    private ExchangeFilterFunction handle4xxErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status code: {}", clientResponse.statusCode());
            if (clientResponse.statusCode().is4xxClientError()) {
                String redirectUrl = "/login";
                return Mono.error(new RedirectionException(redirectUrl));
            }
            if (clientResponse.statusCode().value() == 401 ||
                    clientResponse.statusCode().value() == 403) {
                String redirectUrl = "/login";
                return Mono.error(new AuthorizedException(ErrorCode.INVALID_TOKEN,redirectUrl));
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