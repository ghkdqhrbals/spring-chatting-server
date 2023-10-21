package chatting.chat.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Autowired
    private MyCookieStore cookieStore; // 쿠키 저장소
    @Value("${backend.api.gateway}")
    private String backEntry;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .baseUrl(backEntry)
                .filter(addCookiesFilterFunction());
    }

    @Component
    public class MyCookieStore {
        private String accessToken;
        private String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    private ExchangeFilterFunction addCookiesFilterFunction() {
        return (clientRequest, next) -> {
            String accessToken = cookieStore.getAccessToken();
            String refreshToken = cookieStore.getRefreshToken();

            if (accessToken != null) {
                ClientRequest updatedRequest = ClientRequest.from(clientRequest)
                        .header(HttpHeaders.COOKIE, "accessToken=" + accessToken)
                        .build();
                updatedRequest.headers().add(HttpHeaders.COOKIE, "refreshToken=" + refreshToken);

                return next.exchange(updatedRequest);
            } else {
                return next.exchange(clientRequest);
            }
        };
    }

}