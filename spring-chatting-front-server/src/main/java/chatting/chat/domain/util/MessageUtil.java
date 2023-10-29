package chatting.chat.domain.util;

import chatting.chat.web.error.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(AppException exception, Object... args) {
        return messageSource.getMessage(exception.getErrorCode().getDetail(), args, null);
    }
}
