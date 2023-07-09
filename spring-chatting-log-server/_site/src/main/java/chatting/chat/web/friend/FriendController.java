package chatting.chat.web.friend;


import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.web.filters.cons.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/friend")
public class FriendController {

    private FriendService friendService;
    private WebClient webClient;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    // WebClient 초기화
    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.create("http://localhost:8080");
    }

    // 친구 추가
    @GetMapping("/add")
    public String addFriend(@ModelAttribute("friendForm") FriendForm form){
        return "users/addFriendForm";
    }

    // 친구 추가
    @PostMapping("/add")
    public String addFriendForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User loginUser,
                                @Valid @ModelAttribute("friendForm") FriendForm form,
                                BindingResult bindingResult,
                                Model model, HttpSession session){

        if (bindingResult.hasErrors()){
            return "users/addFriendForm";
        }

        // 존재하는 유저인지 확인(userController에게 api요청)
        // 비동기X
        Optional block = webClient
                .mutate()
                .build()
                .get()
                .uri("/users/api/exist/"+form.getFriendId())
                .cookie("JSESSIONID",session.getId())
                .retrieve()
                .bodyToMono(Optional.class)
                .block(Duration.ofMinutes(3)); // 비동기 사용 X

        // toStream()이나 block()을 사용하게 되면 결과가 도착할 때까지 기다려야한다.
        // 논블로킹 방식은 subscribe(result -> ... )로 사용

        if (!block.isPresent()){
            bindingResult.rejectValue("friendId","exist.user","exist");
            return "users/addFriendForm";
        }

        // 자기 자신인지 확인
        String userId = loginUser.getUserId();
        String friendId = form.getFriendId();
        if (userId.equals(friendId)){
            bindingResult.rejectValue("friendId","myself","자기자신을 친구로 추가할 수 없습니다");
            return "users/addFriendForm";
        }

        String errorCode = friendService.save(loginUser.getUserId(), form.getFriendId());

        if (errorCode == "1" || errorCode == "2"){
            bindingResult.rejectValue("friendId","exist.inFriend");
            return "users/addFriendForm";
        }

        if (errorCode == "3"){
            bindingResult.rejectValue("friendId","exist.inFriend.e","저장 에러");
            return "users/addFriendForm";
        }

        return "redirect:/users";
    }


}
