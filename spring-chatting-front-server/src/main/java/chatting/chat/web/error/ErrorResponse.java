package chatting.chat.web.error;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse implements Serializable {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;

    @Builder
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String code, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
    }
}