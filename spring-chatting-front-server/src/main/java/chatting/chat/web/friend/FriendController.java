package chatting.chat.web.friend;

import chatting.chat.domain.util.MessageUtil;
import chatting.chat.web.friend.dto.FriendForm;
import chatting.chat.web.global.CommonModel;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.friend.FriendRequest.NewFriendDTO;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
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

import javax.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private MessageUtil messageUtil;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
    }

    @GetMapping
    public String addFriend(@ModelAttribute("friendForm") FriendForm form, Model model) {
        CommonModel.addCommonModel(model);
        return "friends/friendAddForm";
    }

    @PostMapping
    public String addFriendForm(@Validated @ModelAttribute("friendForm") FriendForm form,
        BindingResult bindingResult, HttpServletRequest request) {
        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");

        log.trace("Add friends");

        if (bindingResult.hasErrors()) {
            return "friends/friendAddForm";
        }

        try{
            webClientBuilder
                .build()
                .post()
                .uri("/chat/friend")
                .bodyValue(NewFriendDTO.builder()
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
                        return Mono.error(new CustomException(ErrorCode.CANNOT_FIND_USER));
                    })
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
                .bodyToMono(String.class).block();
        }catch (CustomException e){
            String message = messageUtil.getMessage(e, form.getFriendId());
            log.trace("CustomException : {}",message);
            bindingResult.reject( e.getErrorCode().getDetail(), new Object[]{form.getFriendId(),"CHATTING"}, message);
            return "friends/friendAddForm";
        }


        return "redirect:/";
    }

}
