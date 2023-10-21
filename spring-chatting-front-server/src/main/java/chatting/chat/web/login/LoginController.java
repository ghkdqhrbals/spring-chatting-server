package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.login.dto.LoginRequestDto;
import chatting.chat.web.login.dto.LoginResponseDto;
import com.example.commondto.token.TokenConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Slf4j
@Controller
public class LoginController {

    private WebClient webClient;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info("Connected gateway : "+backEntry);
        this.webClient = WebClient.create(backEntry);
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                            BindingResult bindingResult,
                            HttpServletRequest request,
                            HttpServletResponse httpServletResponse,
                            @RequestParam(defaultValue = "/") String redirectURL){

        System.out.println("bindingResult.getObjectName() = " + bindingResult.getObjectName());
        System.out.println("bindingResult.getTarget() = " + bindingResult.getTarget());

        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        User user = new User();
        LoginRequestDto req = new LoginRequestDto();

        req.setUsername(form.getLoginId());
        req.setPassword(form.getPassword());


        try{
            ClientResponse response = webClient.mutate()
                    .build()
                    .post()
                    .uri("/user/login")
                    .bodyValue(req)
                    .exchange()
                    .block();

            // 응답에서 쿠키 추출
            MultiValueMap<String, ResponseCookie> cookies = response.cookies();
            ResponseCookie accessTokenCookie = cookies.getFirst("accessToken");
            ResponseCookie refreshTokenCookie = cookies.getFirst("refreshToken");

            log.info(accessTokenCookie.toString());
            log.info(refreshTokenCookie.toString());

            // 클라이언트로 응답을 보낼 때 쿠키 추가

            // accessToken=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYWEiLCJwZXJtaXNzaW9ucyI6IlJPTEVfVVNFUiIsImlhdCI6MTY5Nzg3MzU1MSwiZXhwIjoxNjk3ODczODUxfQ.rZyIaKfw9Qzxod89sWjrbau6W8m0lzDJPCAy6FY9wULrGlGsp9bkGxMaKWzWHOetKVwxWUTSLIq8wb8CvkBLPQ; Path=/; HttpOnly; SameSite=None
            httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        } catch (CustomThrowableException e) {
            if ("Invalid Credentials".equals(e.getErrorResponse().getMessage())) {
                bindingResult.rejectValue("password", null, e.getErrorResponse().getMessage());
            }

            return "login/loginForm";
        }

        return "login/loginForm";
        // logic
//        log.info(redirectURL);
//        return "redirect:"+redirectURL;
    }

    @GetMapping("/logout")
    public String loginForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser, HttpSession session) {
        try{
            webClient.mutate()
                    .build()
                    .get()
                    .uri("/user/logout")
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            response -> response.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToMono(String.class)
                    .subscribe(log::info);

            session.removeAttribute(SessionConst.LOGIN_MEMBER);
        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getMessage());
            return "redirect:/";
        }
        return "redirect:/";
    }


}
