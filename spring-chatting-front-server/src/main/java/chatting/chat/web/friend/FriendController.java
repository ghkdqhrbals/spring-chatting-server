package chatting.chat.web.friend;

import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.dto.RequestAddFriendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private WebClient webClient;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
        this.webClient = WebClient.create(backEntry);
    }

    // 친구 추가
    @GetMapping
    public String addFriend(@ModelAttribute("friendForm") FriendForm form){
        return "users/addFriendForm";
    }

    // 친구 추가
    @PostMapping
    public String addFriendForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User loginUser,
                                @Valid @ModelAttribute("friendForm") FriendForm form,
                                BindingResult bindingResult,
                                Model model, HttpSession session){

        if (bindingResult.hasErrors()){
            return "users/addFriendForm";
        }


        try{
            String response = webClient.mutate()
                    .build()
                    .post()
                    .uri("/chat/friend")
                    .bodyValue(new RequestAddFriendDTO(loginUser.getUserId(), Arrays.asList(form.getFriendId())))
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToMono(String.class).block();
            log.info(response);

        }catch (CustomThrowableException e){
            bindingResult.rejectValue("friendId", null, e.getErrorResponse().getMessage());
            return "users/addFriendForm";
        }

        return "redirect:/"; // TODO
    }

}
