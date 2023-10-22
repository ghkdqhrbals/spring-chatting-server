package chatting.chat.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizedException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String redirectUrl;
}
