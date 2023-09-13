package chatting.chat.web.filters.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class URLCheckFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.trace("Request in {} {}",request.getMethod(), requestURI);
        return true;

//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
//            response.sendRedirect("/login");
//            log.info("인증 체크 인터셉터 실패 {}", requestURI);
//            return false;
//        }
//        log.info("인증 체크 인터셉터 성공 {}", requestURI);
//        return true;
    }
}
