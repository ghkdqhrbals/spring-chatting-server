package chatting.chat.web.user.service;

import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ResponseGetUser> getUserInfo(String accessToken, String refreshToken) {
        return webClientBuilder.build()
            .get()
            .uri("/chat/user")
            .cookies(c -> {
                c.add("accessToken", accessToken);
                c.add("refreshToken", refreshToken);
            })
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, (response) -> {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            })
            .bodyToMono(ResponseGetUser.class);
    }


}
