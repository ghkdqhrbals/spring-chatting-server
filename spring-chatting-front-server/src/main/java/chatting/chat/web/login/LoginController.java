package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.domain.login.LoginService;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Slf4j
@Controller

public class LoginController extends KafkaTopicConst {
    private final LoginService loginService;
    private final WebClient webClient;

    public LoginController(LoginService loginService, WebClient webClient) {
        this.loginService = loginService;
        this.webClient = webClient;
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

        Mono<Object> objectMono = webClient.mutate()
                .baseUrl("https://localhost:8080")
                .build()
                .get()
                .uri("/auth/login" + form.getLoginId() + "?userPw=" + form.getPassword())
                .retrieve()
                .onStatus(
                        HttpStatus.OK::equals,
                        response -> response.bodyToMono(User.class).map(Exception::new)
                )
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(Exception::new)
                )
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(Exception::new)
                )
                .bodyToMono(Object.class);


        log.info("return 받은 것들",stringMono.toString());


        RequestLoginDTO req = new RequestLoginDTO();
        req.setUserId(form.getLoginId());
        req.setUserPw(form.getPassword());

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
        HttpSession session = request.getSession(true);


        // 세션에 User정보 넣어둠
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginUser);
        log.info(redirectURL);
        return "redirect:"+redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        //세션이 없으면 생성하는 default를 false로 둔다
        HttpSession session = request.getSession(false);

        if (session != null){
            User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
            loginService.logout(user);
            session.invalidate();
        }
        return "redirect:/";
    }



}
