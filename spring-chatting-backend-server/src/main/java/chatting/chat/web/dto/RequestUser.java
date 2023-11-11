package chatting.chat.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestUser {
    @NotNull(message = "user id must not be null")
    @Size(min = 3, message = "user id must not be less than 3")
    private String userId;

    @NotNull(message = "user id must not be null")
    @Size(min = 4, message = "user password must not be less than 4")
    private String userPw;
    @NotNull(message = "email should be not null")
    @Email
    private String email;
    @NotNull(message = "name should be not null")
    @Size(min = 3, message = "user name must not be less than 3")
    private String userName;

    @NotNull(message = "role should be not null")
    @Size(min = 3, message = "role must not be less than 3")
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestUser vo = (RequestUser) o;
        return Objects.equals(userId, vo.userId) &&
                Objects.equals(userPw, vo.userPw) &&
                Objects.equals(email, vo.email) &&
                Objects.equals(userName, vo.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId+userPw+email+userName);
    }
}
