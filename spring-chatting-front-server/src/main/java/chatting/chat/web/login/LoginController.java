package chatting.chat.web.login;

import chatting.chat.domain.data.User;
import chatting.chat.domain.login.LoginService;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestLoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController extends KafkaTopicConst {
    private final LoginService loginService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

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

        RequestLoginDTO req = new RequestLoginDTO();
        req.setUserId(form.getLoginId());
        req.setUserPw(form.getPassword());

        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_LOGIN_REQUEST, form.getLoginId(), req);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message: {}", ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Sent message with topic: {}, key: {}, offset: {}, partition: {}",TOPIC_LOGIN_REQUEST, req.getUserId(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });

        // ?????? ????????????
        // ??? ???????????? ???????????? ??????


        User loginUser = loginService.login(form.getLoginId(), form.getPassword());

        // loginForm??? @NotEmpty??? ?????? annotation ??????????????????.
        // Form?????? ????????? ???????????? ???????????? ??? ????????????. ???????????? bindingReulst??? ??????????????????.
        if (loginUser == null){
            bindingResult.reject("loginFail", "????????? or ??????????????? ?????? ????????????.");
            return "login/loginForm";
        }

        // ?????? ????????? ??????
        HttpSession session = request.getSession(true);


        // ????????? User?????? ?????????
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginUser);
        log.info(redirectURL);
        return "redirect:"+redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        //????????? ????????? ???????????? default??? false??? ??????
        HttpSession session = request.getSession(false);

        if (session != null){
            User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
            loginService.logout(user);
            session.invalidate();
        }
        return "redirect:/";
    }



}
