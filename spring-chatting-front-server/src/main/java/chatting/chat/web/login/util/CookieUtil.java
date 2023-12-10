package chatting.chat.web.login.util;



import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.thymeleaf.util.ArrayUtils;

public class CookieUtil {
    public static void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String getCookie(ClientRequest request, String cookieName) {
        MultiValueMap<String, String> cookies = request.cookies();
        return cookies.getFirst(cookieName);
    }

    public static String getCookie(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)){
            return null;
        }
        Cookie findCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findFirst().orElse(null);
        if (findCookie==null){
            return null;
        }
        return findCookie.getValue();
    }
}
