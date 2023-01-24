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

//        RequestLoginDTO req = new RequestLoginDTO();
//        req.setUserId(form.getLoginId());
//        req.setUserPw(form.getPassword());

//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_LOGIN_REQUEST, form.getLoginId(), req);
//
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                log.error("Unable to send message: {}", ex.getMessage());
//            }
//            @Override
//            public void onSuccess(SendResult<String, Object> result) {
//                log.info("Sent message with topic: {}, key: {}, offset: {}, partition: {}",TOPIC_LOGIN_REQUEST, req.getUserId(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
//            }
//        });

        // 소켓 열어놓기
        // 값 도착하면 유저에게 전달

        // loginForm에 @NotEmpty와 같은 annotation 입력하였었음.
        // Form으로 넘어온 데이터가 비어있을 때 오류발생. 오류들은 bindingReulst에 보관되어있음.
//        if (loginUser == null){
//            bindingResult.reject("loginFail", "아이디 or 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }

        // 이후 로그인 성공
//        HttpSession session = request.getSession(true);


        // 세션에 User정보 넣어둠
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginUser);
//        log.info(redirectURL);
//        return "redirect:"+redirectURL;
    }

//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request){
//        //세션이 없으면 생성하는 default를 false로 둔다
//        HttpSession session = request.getSession(false);
//
//        if (session != null){
//            User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
//            loginService.logout(user);
//            session.invalidate();
//        }
//        return "redirect:/";
//    }



}
