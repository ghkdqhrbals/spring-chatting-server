package chatting.chat.web.filter;

import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContext {

    private static final ThreadLocal<String> userIdThreadLocal = new ThreadLocal<>();

    public static void setUserId(String userId) {
        log.trace("ThreadLocal userId saved : {}", userId);
        userIdThreadLocal.set(userId);
    }

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

