package chatting.chat.web.filters.filter;

import chatting.chat.web.filters.cons.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Slf4j
public class LoginCheckFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        Cookie authCookie = WebUtils.getCookie(request, "jwttoken");
        if ( authCookie == null ){
            response.sendRedirect("/login");
            log.info("인증 체크 인터셉터 실패 {}", requestURI);
            return false;
        }
        log.info("인증 체크 인터셉터 성공 {}", requestURI);
        return true;
    }

}
