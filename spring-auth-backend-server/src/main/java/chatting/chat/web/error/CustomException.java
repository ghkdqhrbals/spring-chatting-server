package chatting.chat.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException { // RuntimeException 상속으로 Transactional Rollback
    private final ErrorCode errorCode;
}