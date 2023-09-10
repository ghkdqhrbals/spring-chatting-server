package chatting.chat.web.filters.filter;

import chatting.chat.web.filters.cons.SessionConst;
import com.example.commondto.token.TokenConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

@Slf4j
public class LoginCheckFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("인터셉터 실행 {}", requestURI);

        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            response.sendRedirect("/login");
            return false;
        }

        ArrayList<String> accessTokens = Arrays.stream(request.getCookies()).filter(cookie -> {
            log.info("쿠키정보: {}",cookie.getValue());
            if (cookie.getName().equals(TokenConst.keyName)) {
                return true;
            }
            return false;
        }).map(c -> c.getValue()).collect(Collectors.toCollection(ArrayList::new));

        if (accessTokens.size()==0){
            log.trace("쿠키에서 토큰정보를 찾을 수 없습니다");
            return false;
        }

        String accessToken = accessTokens.get(0);



        if (accessToken == null){
            log.trace("토큰 값이 비어있습니다");
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }

        log.trace("쿠키정보 존재");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        System.out.println("쿠키에서 토큰정보를 찾았습니다. "+new String(decoder.decode(accessToken)));

//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER)
//                == null) {
//            //로그인으로 redirect
////            response.sendRedirect("/login?redirectURL=" + requestURI);
//            response.sendRedirect("/login");
//            log.info("인증 체크 인터셉터 실패 {}", requestURI);
//            return false;
//        }
//        log.info("인증 체크 인터셉터 성공 {}", requestURI);
        return true;
    }

}
