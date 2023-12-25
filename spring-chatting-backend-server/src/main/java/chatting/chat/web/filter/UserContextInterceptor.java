package chatting.chat.web.filter;

import chatting.chat.web.sessionCluster.redis.UserRedisSession;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import chatting.chat.web.sessionCluster.redis.util.RedisUtil;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.*;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserRedisSessionRepository userRedisSessionRepository;

    private final Map<String, Set<HttpMethod>> whiteList = new HashMap<>();
//    private final List<String> whiteList = Arrays.asList("/health","/user");

    public UserContextInterceptor(UserRedisSessionRepository userRedisSessionRepository) {
        this.userRedisSessionRepository = userRedisSessionRepository;

        addWhiteList("/actuator/health", HttpMethod.GET);
        addWhiteList("/user", HttpMethod.POST);
        addWhiteList("/health", HttpMethod.GET);
    }

    private void addWhiteList(String path, HttpMethod method) {
        if (whiteList.containsKey(path)) {
            whiteList.get(path).add(method);
        } else {
            whiteList.put(path, Set.of(method));
        }
    }

    @Override
    @Timed(value = "interceptor.preHandle")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.trace("requestURI: {}", requestURI);
        if (whiteList.get(requestURI).stream().anyMatch((method) -> {
            if (method.equals(HttpMethod.valueOf(request.getMethod()))) {
                return true;
            }
            return false;
        })) {
            log.debug("whiteList pass : {}", requestURI);
            return true;
        }

        String userId = extractUserIdFromRequest(request);
        if (userId == null) {
            response.sendError(401);
            return false;
        }
        UserContext.setUserId(userId);
        log.trace("ThreadLocal save userId: {}", userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    @Timed(value = "interceptor.extractUserIdFromRequest")
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
                Optional<UserRedisSession> findUser = userRedisSessionRepository.findById(
                    findCookie.getValue());
                if (findUser.isPresent()) {
                    log.info("user found in redis session");
                    return findUser.get().getUserId();
                }
                log.info("user not found in redis session");
                return null;
            }
        }
        log.info("cookie not found");
        return null;
    }
}