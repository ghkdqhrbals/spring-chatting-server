package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.user.LoginRequestDto;
import chatting.chat.web.user.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
            LoginResponseDto res = webClient.mutate()
                    .baseUrl("http://127.0.0.1:8000")
                    .build()
                    .post()
                    .uri("/user/login")
                    .bodyValue(req)
                    .retrieve()
                    .onStatus(
                            HttpStatus.NOT_FOUND::equals,
                            response -> response.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .onStatus(
                            HttpStatus.INTERNAL_SERVER_ERROR::equals,
                            response -> response.bodyToMono(ErrorResponse.class).map(e -> new CustomThrowableException(e)))
                    .bodyToMono(LoginResponseDto.class)
                    .block();
//            log.info("Retrieve TOKEN : ");
//            log.info("Retrieve TOKEN : {}",res.getToken());


            String tokenCookie = URLEncoder.encode("Bearer " + res.getToken(), "utf-16").replaceAll("\\+", "%20");;
            Cookie myCookie = new Cookie("jwttoken", tokenCookie);
            myCookie.setMaxAge(10000);
            myCookie.setPath("/");

            httpServletResponse.addCookie(myCookie);
//            httpServletResponse.addHeader("Set-Cookie","Bearer "+res.getToken());
        }catch (CustomThrowableException e){

            if ("Invalid Credentials".equals(e.getErrorResponse().getMessage())){
                bindingResult.rejectValue("password", null,e.getErrorResponse().getMessage());
            }

            return "login/loginForm";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // logic
//        HttpSession session = request.getSession(true);
//        session.setAttribute(SessionConst.LOGIN_MEMBER, user);


//        log.info("redirect to = {} userId:{}, userPw:{}",redirectURL,user.getUserId(),user.getUserPw());
        return "redirect:"+redirectURL;

    }

    @GetMapping("/logout")
    public String loginForm(HttpServletResponse httpServletResponse) {
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

            Cookie cookie = new Cookie("jwttoken", "");
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
            
        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getMessage());
            return "redirect:/";
        }
        return "redirect:/";
    }


}
