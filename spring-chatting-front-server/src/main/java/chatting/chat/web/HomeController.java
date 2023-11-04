package chatting.chat.web;

import chatting.chat.domain.util.MessageUtil;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.error.AppException;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.friend.FriendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class HomeController {


    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private MessageUtil messageUtil;
    @Value("${backend.api.gateway}")
    private String backEntry;


    @GetMapping("/")
    public String mainHome(HttpServletRequest request, Model model) {
        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");
        log.trace("accessToken: {}", accessToken);
        log.trace("refreshToken: {}", refreshToken);

        if (accessToken == null || refreshToken == null) {
            return "redirect:/login";
        }

        try {
            ResponseGetUser me = webClientBuilder
                .build()
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
                .bodyToMono(ResponseGetUser.class).log().block();
            model.addAttribute("userName", me.getUserName());
            model.addAttribute("userDescription", me.getUserStatus());
            Flux<FriendResponse.FriendDTO> resGetFriend = webClientBuilder
                .build()
                .get()
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

            List<FriendResponse.FriendDTO> readers = resGetFriend.collect(Collectors.toList())
                .share().block();
            readers.stream().forEach(friendDTO -> {
                log.info("friendDTO: {}", friendDTO);
            });
            model.addAttribute("friends", readers);

        } catch (AppException e) {
            log.info("Exception Message {}", messageUtil.getMessage(e));
            return "redirect:/login";
        }

        return "friends/friends";
    }
}
