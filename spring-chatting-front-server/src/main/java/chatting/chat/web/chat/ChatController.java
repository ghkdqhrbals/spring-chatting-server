package chatting.chat.web.chat;

import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ChatRecord;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.global.CommonModel;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.chat.ChatRequest;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTOsWithUser;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
@AllArgsConstructor
public class ChatController {

    private final WebClient.Builder webClientBuilder;

    @GetMapping("/chat")
    public String chattingRoom(@RequestParam Long roomId, @RequestParam String roomName,
        Model model, HttpServletRequest request) {
        CommonModel.addCommonModel(model);

        // get records from chat server
        ChatRecordDTOsWithUser response = webClientBuilder
            .build()
            .get()
            .uri("/chat/chats?roomId=" + roomId)
            .cookies(c -> {
                // get refreshToken from Cookie and add it to request header
                c.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
                c.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
            })
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
            .bodyToMono(ChatRequest.ChatRecordDTOsWithUser.class).block();

        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", roomName);
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("userName", response.getUserName());
        model.addAttribute("records", response.getRecords());

        return "chat/chat";
    }
}
