package chatting.chat.web;

import chatting.chat.domain.util.MessageUtil;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.error.AppException;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.friend.service.FriendService;
import chatting.chat.web.global.CommonModel;
import chatting.chat.web.login.util.CookieUtil;
import chatting.chat.web.user.service.UserService;
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
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class HomeController {


    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageUtil messageUtil;
    @Value("${backend.api.gateway}")
    private String backEntry;


    @GetMapping("/")
    public Mono<String> mainHome(HttpServletRequest request, Model model) {
        CommonModel.addCommonModel(model);
        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");

        if (accessToken == null || refreshToken == null) {
            return Mono.just("redirect:/login");
        }

        try {
            Mono<ResponseGetUser> userInfo = userService.getUserInfo(accessToken, refreshToken);
            Flux<FriendResponse.FriendDTO> resGetFriend = friendService.getMyFriends(accessToken,
                refreshToken);

            return Mono.zip(userInfo, resGetFriend.collectList())
                .doOnNext(tuple -> {
                    ResponseGetUser me = tuple.getT1();
                    List<FriendResponse.FriendDTO> friends = tuple.getT2();

                    model.addAttribute("userName", me.getUserName());
                    model.addAttribute("userDescription", me.getUserStatus());
                    model.addAttribute("friends", friends);
                })
                .then(Mono.just("friends/friends")).log();

        } catch (AppException e) {
            log.info("Exception Message {}", messageUtil.getMessage(e));
            return Mono.just("redirect:/login");
        }
    }
}
