package chatting.chat.web.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class UserForm {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String userPw;

    @NotEmpty
    private String email;

    @NotEmpty
    private String userName;

}
