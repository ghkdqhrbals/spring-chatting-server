package chatting.chat.web.user.service;

import chatting.chat.web.dto.ResponseGetUser;

import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ResponseGetUser> getUserInfo(HttpServletRequest request) {
        return webClientBuilder.build()
            .get()
            .uri("/chat/user")
            .cookies(c -> {
                c.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
                c.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
            })
            .retrieve()
            .bodyToMono(ResponseGetUser.class);
    }


}
