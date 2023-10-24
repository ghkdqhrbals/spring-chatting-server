package chatting.chat.web.filter;

import chatting.chat.domain.redis.UserRefreshToken;
import chatting.chat.domain.redis.UserRefreshTokenRedisRepository;
import chatting.chat.domain.redis.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserRefreshTokenRedisRepository userRefreshTokenRedisRepository;
    private final RedisUtil redisUtil;


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
        log.info("cookies: {}", Arrays.stream(cookies).toList());
        log.info("cookies length: {}", cookies.length);
        if (cookies != null) {
            Cookie findCookie = Arrays.stream(cookies)
                    .filter((cookie) -> {
                        return "refreshToken".equals(cookie.getName());
                    })
                    .findFirst()
                    .orElse(null);
            if (findCookie != null) {
                log.info("findCookie: {}", findCookie.getValue());
                log.info("get all keys: {}", redisUtil.getAllKeys());
                log.info("is exsist? {}",redisUtil.getData(findCookie.getValue(), UserRefreshToken.class).isPresent());
                log.info("find userRefreshToken {}",redisUtil.getData(findCookie.getValue(), UserRefreshToken.class).get());
                userRefreshTokenRedisRepository.findAll().forEach((userRefreshToken) -> {
                    log.info("userRefreshToken: {}", userRefreshToken);
                });

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