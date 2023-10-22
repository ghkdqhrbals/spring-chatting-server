package chatting.chat.web.filter;

import chatting.chat.domain.redis.UserRefreshToken;
import chatting.chat.domain.redis.UserRefreshTokenRedisRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserRefreshTokenRedisRepository userRefreshTokenRedisRepository;

    public UserContextInterceptor(UserRefreshTokenRedisRepository userRefreshTokenRedisRepository) {
        this.userRefreshTokenRedisRepository = userRefreshTokenRedisRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = extractUserIdFromRequest(request);
        if (userId==null){
            response.sendError(401);
            return false;
        }
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        log.info("cookies: {}", cookies);
        if (cookies != null) {
            Cookie findCookie = Arrays.stream(cookies)
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);
            if (findCookie != null) {
                Optional<UserRefreshToken> findUser = userRefreshTokenRedisRepository.findById(findCookie.getValue());
                if (findUser.isPresent()) {
                    log.info("user found in redis session");
                    return findUser.get().getUserId();
                }
                log.info("user not found in redis session");
                return null;
            }
        }
        return null;
    }
}