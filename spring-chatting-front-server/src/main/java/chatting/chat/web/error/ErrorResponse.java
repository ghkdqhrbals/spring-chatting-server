package chatting.chat.web.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String error;
    private String code;
    private String message;

    private String requestId;

    public ErrorResponse() {
    }

    public ErrorResponse(Date timestamp, int status, String error, String code, String message, String requestId) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.requestId = requestId;
    }
}