package chatting.chat.web.friend.service;

import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
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
                HttpStatus::is4xxClientError, (r) -> {
                    throw new CustomException(ErrorCode.INVALID_TOKEN);
                })
            .bodyToFlux(FriendResponse.FriendDTO.class);
    }
}
