package chatting.chat.web;

import chatting.chat.domain.data.User;
import chatting.chat.web.filters.cons.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser,
            Model model) {

        //세션에 회원 데이터가 없으면 home
        if (loginUser == null) {
            log.info("세션에 (USER) 데이터가 없습니다");
            model.addAttribute("status",false);
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("user", loginUser);
        model.addAttribute("status",true);
        return "home";
    }
}
