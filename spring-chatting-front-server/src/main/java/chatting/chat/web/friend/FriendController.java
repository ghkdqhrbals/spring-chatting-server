package chatting.chat.web.friend;

import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.error.AppException;
import chatting.chat.web.error.AuthorizedException;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.dto.RequestAddFriendDTO;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.friend.FriendRequest;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
    }

    // 친구 추가
    @GetMapping
    public String addFriend(@ModelAttribute("friendForm") FriendForm form) {
        return "users/addFriendForm";
    }

    // 친구 추가
    @PostMapping
    public String addFriendForm(@Validated @ModelAttribute("friendForm") FriendForm form,
        HttpServletRequest request, BindingResult bindingResult) {
        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");

        log.trace("add friends!");

        if (bindingResult.hasErrors()) {
            return "users/addFriendForm";
        }

        try {
            String response = webClientBuilder
                .build()
                .post()
                .uri("/chat/friend")
                .bodyValue(FriendRequest.NewFriendDTO.builder()
                    .friendId(form.getFriendId())
                    .build())
                .cookies(c -> {
                    c.add("accessToken", accessToken);
                    c.add("refreshToken", refreshToken);
                })
                .retrieve()
                .onStatus(
                    (status) -> status == HttpStatus.NOT_FOUND,
                    (res) -> {
                        throw new CustomException(ErrorCode.CANNOT_FIND_USER);
                    })
                .bodyToMono(String.class).block();
            log.info(response);

        } catch (AuthorizedException authorizedException) {
            return "redirect:" + authorizedException.getRedirectUrl();
        } catch (CustomException e) {
            bindingResult.rejectValue("friendId", e.getErrorCode().getDetail(),
                new Object[]{form.getFriendId()}, e.getErrorCode().getDetail());
            return "users/addFriendForm";
        }

        return "redirect:/"; // TODO
    }

}
