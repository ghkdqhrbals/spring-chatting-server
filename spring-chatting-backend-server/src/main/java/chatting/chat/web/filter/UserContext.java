package chatting.chat.web.filter;

import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContext {

    private static final ThreadLocal<String> userIdThreadLocal = new ThreadLocal<>();

    /**
     * ThreadLocal에 userId를 저장합니다.
     * @param userId {@link String}
     */
    public static void setUserId(String userId) {
        log.trace("ThreadLocal userId saved : {}", userId);
        userIdThreadLocal.set(userId);
    }

    /**
     * ThreadLocal에 저장된 userId를 반환합니다.
     * @return userId {@link String}
     */
    public static String getUserId() {
        log.trace("ThreadLocal userId : {}", userIdThreadLocal.get());
        if (userIdThreadLocal.get() == null) {
            log.trace("ThreadLocal userId is null");
            throw new CustomException(ErrorCode.CANNOT_FIND_USER);
        }
        return userIdThreadLocal.get();
    }

    public static void clear() {
        userIdThreadLocal.remove();
    }
}

