package chatting.chat.web;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.login.LoginForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
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
    public String mainHome(
            @CookieValue String jwttoken,
            Model model) {

        try{
            ResponseGetUser me = webClient.mutate()
                    .build()
                    .get()
                    .uri("http://localhost:8000/chat/user?userId=" + "ab1234")
                    .header(HttpHeaders.AUTHORIZATION,jwttoken)
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .bodyToMono(ResponseGetUser.class).block();
            model.addAttribute("user",me);

            Flux<ResponseGetFriend> response = webClient.mutate()
                    .build()
                    .get()
                    .uri("http://localhost:8000/chat/friend?userId=" + "ab1234")
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .bodyToFlux(ResponseGetFriend.class);
            List<ResponseGetFriend> readers = response.collect(Collectors.toList())
                    .share().block();
            model.addAttribute("friends",readers);

        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getMessage());
            return "login/loginForm";
        }

        return "users";
    }
}
