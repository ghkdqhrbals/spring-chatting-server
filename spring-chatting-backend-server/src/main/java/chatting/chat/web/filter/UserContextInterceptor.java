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

/**
 * 초기 요청 시, 쿠키에서 userId 를 추출하여 {@link UserRedisSessionRepository} 를 통해 Redis 에서 userId 에 해당하는
 * {@link UserRedisSession} 을 가져옵니다. 이후 {@link UserContext} 에 userId 를 저장합니다.
 */
@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserRedisSessionRepository userRedisSessionRepository;

    private final Map<String, Set<HttpMethod>> whiteList = new HashMap<>();

    /**
     * @param userRedisSessionRepository
     * @implNote WhiteList를 설정할 수 있으며 등록된 path 는 {@link UserContextInterceptor#preHandle} 에서 검증하지
     * 않습니다.
     */
    public UserContextInterceptor(UserRedisSessionRepository userRedisSessionRepository) {
        this.userRedisSessionRepository = userRedisSessionRepository;

        addWhiteList("/actuator/health", HttpMethod.GET);
        addWhiteList("/user", HttpMethod.POST);
        addWhiteList("/health", HttpMethod.GET);
    }

    /**
     * @param path
     * @param method
     * @implNote WhiteList 에 path 를 추가합니다.
     */
    private void addWhiteList(String path, HttpMethod method) {
        if (whiteList.containsKey(path)) {
            whiteList.get(path).add(method);
        } else {
            whiteList.put(path, Set.of(method));
        }
    }

    /**
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the next interceptor or the
     * handler itself.
     * @throws Exception
     * @implNote 요청이 들어올 때마다 userId 를 추출하여 {@link UserContext} 에 저장합니다. 추출된 userId 가 없을 경우 401 을
     * 반환합니다. 또한 Redis 에 refreshToken 이 저장되어있지 않을 때도 401 에러를 반환합니다. 테스트 시 {@link UserContext#setUserId} 로 ThreadLocal 에 저장해주세요.
     */
    @Override
    @Timed(value = "interceptor.preHandle")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (request == null) {
            log.trace("request is null");
            return false;
        }
        String requestURI = request.getRequestURI();
        log.trace("requestURI: {}", requestURI);

        Set<HttpMethod> methods = whiteList.get(requestURI);
        if (methods != null && methods.stream()
            .anyMatch(method -> method.equals(HttpMethod.valueOf(request.getMethod())))) {
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