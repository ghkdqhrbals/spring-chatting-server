package chatting.chat.web.login;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginForm {

    @NotEmpty(message = "{loginForm.loginId.notEmpty}")
    private String loginId;

    @NotEmpty(message = "{loginForm.password.notEmpty}")
    private String password;

}
