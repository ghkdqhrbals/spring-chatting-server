package chatting.chat.web.error;


public class CustomException extends AppException {

    public CustomException(ErrorCode errorCode) {
        super(errorCode);
    }

}