package chatting.chat.web.websocket;

import chatting.chat.web.dto.AddUserResponse;
import chatting.chat.web.dto.ChatMessage;
import chatting.chat.web.dto.ChatRecord;
import chatting.chat.web.dto.RequestAddChatRoomDTO;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.kafka.dto.RequestAddChatMessageDTO;
import chatting.chat.web.login.util.CookieUtil;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.socket.WebSocketSession;

@Controller
@AllArgsConstructor
@Slf4j
public class StompChatController {

    private final WebClient.Builder webClientBuilder;
    private final SimpMessagingTemplate template;

    // "/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage message) {
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(),
            message); // Direct send topic to stomp
    }

    @MessageMapping(value = "/user")
    public void registers(AddUserResponse message) {
        template.convertAndSend("/sub/user/" + message.getUserId(),
            message); // Direct send topic to stomp
    }


    //"/pub/chat/message"
    @MessageMapping(value = "/chat/message")
    public void message(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        log.info(message.getMessage());
        String accessToken = (String) accessor.getSessionAttributes().get("accessToken");
        String refreshToken = (String) accessor.getSessionAttributes().get("refreshToken");

        log.trace("[WEBSOCKET] accessToken: {}, refreshToken: {}", accessToken, refreshToken);


        try {
            webClientBuilder
                .build()
                .post()
                .uri("/chat/chat")
                .cookies(c -> {
                    // get refreshToken from Cookie and add it to request header
                    c.add("refreshToken", refreshToken);
                    c.add("accessToken", accessToken);
                })
                .bodyValue(new RequestAddChatMessageDTO(message.getRoomId(), message.getWriter(),
                    message.getWriterId(), message.getMessage()))
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError,
                    r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToMono(String.class).block();

        } catch (CustomThrowableException e) {
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
        }


        template.convertAndSend("/sub/chat/room/" + message.getRoomId(),
            message); // Direct send topic to stomp
        // kafka topic send
    }
}