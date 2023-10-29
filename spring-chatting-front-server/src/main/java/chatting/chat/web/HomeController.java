package chatting.chat.web;

import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.login.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
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

    private WebClient webClient;
    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.create(backEntry);
    }

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
            ResponseGetUser me = webClient.mutate()
                .baseUrl(backEntry)
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
            Flux<ResponseGetFriend> resGetFriend = webClient.mutate()
                .baseUrl(backEntry)
                .build()
                .get()
                .uri("/chat/friend")
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError, (r) -> {
                        throw new CustomException(ErrorCode.INVALID_TOKEN);
                    })
                .bodyToFlux(ResponseGetFriend.class);

            List<ResponseGetFriend> readers = resGetFriend.collect(Collectors.toList())
                .share().block();
            model.addAttribute("friends", readers);

        } catch (CustomException e) {
            log.info(e.getErrorCode().getDetail());
            return "login/loginForm";
        }

        return "friends/friends";
    }
}
