package chatting.chat.web.error;


public class AuthorizedException extends AppException {
    private final String redirectUrl;

    public AuthorizedException(ErrorCode errorCode, String redirectUrl) {
        super(errorCode);
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
