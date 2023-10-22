package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.login.dto.LoginRequestDto;
import chatting.chat.web.login.dto.LoginResponseDto;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.token.TokenConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    @Autowired
    private WebClient.Builder webClientBuilder;

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

        LoginRequestDto req = new LoginRequestDto();
        req.setUsername(form.getLoginId());
        req.setPassword(form.getPassword());


        try{
            webClientBuilder
                    .build()
                    .post()
                    .uri("/user/login")
                    .bodyValue(req)
                    .retrieve()
                    .onStatus(HttpStatus::is2xxSuccessful, (response) -> {
                        MultiValueMap<String, ResponseCookie> cookies = response.cookies();
                        ResponseCookie accessTokenCookie = cookies.getFirst("accessToken");
                        ResponseCookie refreshTokenCookie = cookies.getFirst("refreshToken");

                        log.info(accessTokenCookie.toString());
                        log.info(refreshTokenCookie.toString());

                        httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

                        return Mono.empty();
                    })
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .bodyToMono(String.class).block();

        } catch (CustomThrowableException e) {
            log.info(e.getErrorResponse().getMessage());
            if ("Invalid Credentials ".equals(e.getErrorResponse().getMessage())) {
                bindingResult.rejectValue("password", null, e.getErrorResponse().getMessage());
            }
            return "login/loginForm";
        }

        // logic
//        log.info(redirectURL);
        return "redirect:"+redirectURL;
    }

    @GetMapping("/logout")
    public String loginForm(HttpServletRequest request, HttpServletResponse httpResponse) {
        try{
            webClientBuilder
                    .build()
                    .post()
                    .uri("/user/logout")
                    .cookies((cookies) -> {
                        cookies.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
                        cookies.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
                    })
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            response -> response.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToMono(String.class)
                    .block();
            CookieUtil.removeCookie(httpResponse, "accessToken");
            CookieUtil.removeCookie(httpResponse, "refreshToken");
        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getMessage());
            return "redirect:/login";
        }
        return "redirect:/login";
    }

}
