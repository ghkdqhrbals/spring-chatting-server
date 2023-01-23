package chatting.chat.web.error;

import lombok.Getter;

@Getter
public class CustomThrowableException extends RuntimeException{
    private ErrorResponse errorResponse;

    public CustomThrowableException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public CustomThrowableException(String message, ErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public CustomThrowableException(String message, Throwable cause, ErrorResponse errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }

    public CustomThrowableException(Throwable cause, ErrorResponse errorResponse) {
        super(cause);
        this.errorResponse = errorResponse;
    }

    public CustomThrowableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorResponse errorResponse) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorResponse = errorResponse;
    }
}
