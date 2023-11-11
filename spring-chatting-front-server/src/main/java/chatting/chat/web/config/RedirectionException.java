package chatting.chat.web.config;

public class RedirectionException extends RuntimeException {

    private final String redirectUrl;

    public RedirectionException(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
