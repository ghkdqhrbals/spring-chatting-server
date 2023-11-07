package chatting.chat.domain.util;

import com.example.commondto.error.AppException;
import com.example.commondto.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageUtil {

    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(AppException exception, Object... args) {
        log.info(exception.getErrorCode().getDetail());
        Object[] obj = args;
        return messageSource.getMessage(exception.getErrorCode().getDetail(), obj, null);
    }

    public String getMessage(CustomException exception, Object... args) {
        log.info(exception.getErrorCode().getDetail());
        Object[] obj = args;
        return messageSource.getMessage(exception.getErrorCode().getDetail(), obj, null);
    }
}
