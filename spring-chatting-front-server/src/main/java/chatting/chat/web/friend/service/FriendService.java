package chatting.chat.web.friend.service;


import chatting.chat.web.error.ErrorResponse;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FriendService {
    @Autowired
    public WebClient.Builder webClientBuilder;

    public Flux<FriendResponse.FriendDTO> getMyFriends(String accessToken, String refreshToken) {
        return webClientBuilder.build().get()
            .uri("/chat/friends")
            .cookies(c -> {
                c.add("accessToken", accessToken);
                c.add("refreshToken", refreshToken);
            })
            .retrieve()
            .onStatus(
                (status) -> status == HttpStatus.BAD_REQUEST,
                (res) -> res.bodyToMono(com.example.commondto.error.ErrorResponse.class)
                    .flatMap(errorResponse -> {
                        log.info("errorResponse: {}", errorResponse);
                        if (errorResponse != null && errorResponse.getCode() != null) {
                            return Mono.error(new CustomException(ErrorCode.valueOf(errorResponse.getCode())));
                        } else {
                            return Mono.error(new CustomException(ErrorCode.BAD_REQUEST_DEFAULT));
                        }
                    }))
            .bodyToFlux(FriendResponse.FriendDTO.class);
    }
}
