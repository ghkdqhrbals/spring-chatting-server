package chatting.chat.web.room;

import chatting.chat.web.dto.ChatRoomDTO;
import chatting.chat.web.dto.RequestAddChatRoomDTO;
import chatting.chat.web.error.CustomThrowableException;

import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorResponse;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.*;
@Service
@AllArgsConstructor
public class RoomService {
    private final WebClient.Builder webClientBuilder;


    public Flux<ChatRoomDTO> getChatRooms(HttpServletRequest request) {
        return webClientBuilder.build().get()
            .uri("/chat/rooms")
            .cookies(c -> {
                c.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
                c.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
            })
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                r -> r.bodyToMono(ErrorResponse.class).map(CustomException::new))
            .bodyToFlux(ChatRoomDTO.class);
    }

    public Flux<ChatRoomDTO> addChatRoom(List<String> friendIds, HttpServletRequest request){
        return webClientBuilder.build()
            .post()
            .uri("/chat/room")
            .bodyValue(new RequestAddChatRoomDTO(friendIds))
            .cookies(c -> {
                c.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
                c.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
            })
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                r -> r.bodyToMono(ErrorResponse.class).map(CustomException::new))
            .bodyToFlux(ChatRoomDTO.class);
    }

}
