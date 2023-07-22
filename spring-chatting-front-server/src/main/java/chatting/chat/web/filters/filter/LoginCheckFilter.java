package chatting.chat.web.filters.filter;

import chatting.chat.web.filters.cons.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER)
                == null) {
            //로그인으로 redirect
//            response.sendRedirect("/login?redirectURL=" + requestURI);
            response.sendRedirect("/login");
            log.info("인증 체크 인터셉터 실패 {}", requestURI);
            return false;
        }
        log.info("인증 체크 인터셉터 성공 {}", requestURI);
        return true;
    }

}
