package chatting.chat.web.error;

import chatting.chat.web.config.RedirectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(RedirectionException.class)
    public String handleRedirectionException(RedirectionException ex) {
        log.info("[RedirectionException] Redirect to {}", ex.getRedirectUrl());

        return "redirect:" + ex.getRedirectUrl();
    }

    @ExceptionHandler(AuthorizedException.class)
    public String handleAuthorizedException(AuthorizedException ex) {
        log.info("[AuthorizationException] Redirect to {}", ex.getRedirectUrl());
        return "redirect:" + ex.getRedirectUrl();
    }
}