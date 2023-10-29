package chatting.chat.web.filter;

import chatting.chat.web.sessionCluster.redis.UserRedisSession;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import chatting.chat.web.sessionCluster.redis.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserRedisSessionRepository userRedisSessionRepository;
    private final RedisUtil redisUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = extractUserIdFromRequest(request);
        if (userId==null){
            response.sendError(401);
            return false;
        }
        UserContext.setUserId(userId);
        log.trace("ThreadLocal save userId: {}",userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie findCookie = Arrays.stream(cookies)
                    .filter((cookie) -> {
                        return "refreshToken".equals(cookie.getName());
                    })
                    .findFirst()
                    .orElse(null);
            if (findCookie != null) {
                Optional<UserRedisSession> findUser = userRedisSessionRepository.findById(findCookie.getValue());
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