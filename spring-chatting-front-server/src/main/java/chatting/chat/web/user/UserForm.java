package chatting.chat.web.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class UserForm {

    @NotEmpty(message = "{userForm.userId.notEmpty}")
    private String userId;
    @NotEmpty(message = "{userForm.userPw.notEmpty}")
    private String userPw;
    @NotEmpty(message="{userForm.email.notEmpty}")
    private String email;
    @NotEmpty(message="{userForm.username.notEmpty}")
    private String userName;
}
