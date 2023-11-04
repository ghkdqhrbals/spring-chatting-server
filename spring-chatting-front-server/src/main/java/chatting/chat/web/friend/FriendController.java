package chatting.chat.web.friend;

import chatting.chat.web.error.AuthorizedException;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.friend.dto.FriendForm;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.friend.FriendRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

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
        return "friends/friendAddForm";
    }

    // 친구 추가
    @PostMapping
    public String addFriendForm(@Validated @ModelAttribute("friendForm") FriendForm form,
        BindingResult bindingResult, HttpServletRequest request) {
        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");

        log.trace("Add friends");

        if (bindingResult.hasErrors()) {
            return "friends/friendAddForm";
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
            return "friends/friendAddForm";
        }

        return "redirect:/"; // TODO
    }

}
