package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Slf4j
@Controller
public class LoginController {

    private WebClient webClient;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
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
                            @RequestParam(defaultValue = "/") String redirectURL){

        System.out.println("bindingResult.getObjectName() = " + bindingResult.getObjectName());
        System.out.println("bindingResult.getTarget() = " + bindingResult.getTarget());

        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        User user = new User();
        try{
            user = webClient.mutate()
                    .baseUrl("http://127.0.0.1:8060")
                    .build()
                    .get()
                    .uri("/auth/login?userId=" + form.getLoginId() + "&userPw=" + form.getPassword())
                    .retrieve()
                    .onStatus(
                            HttpStatus.NOT_FOUND::equals,
                            response -> response.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .bodyToMono(User.class)
                    .block();
        }catch (CustomThrowableException e){
            if (e.getErrorResponse().getStatus()==HttpStatus.NOT_FOUND.value()){
                bindingResult.rejectValue("loginId", null, e.getErrorResponse().getMessage());
            }
            if (e.getErrorResponse().getStatus()==HttpStatus.UNAUTHORIZED.value()){
                bindingResult.rejectValue("password", null,e.getErrorResponse().getMessage());
            }
            return "login/loginForm";
        }

        // logic
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, user);
        log.info(redirectURL);
        return "redirect:"+redirectURL;

    }

    @GetMapping("/logout")
    public String loginForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser, HttpSession session) {
        try{
            webClient.mutate()
                    .build()
                    .get()
                    .uri("/auth/logout?userId=" + loginUser.getUserId())
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
