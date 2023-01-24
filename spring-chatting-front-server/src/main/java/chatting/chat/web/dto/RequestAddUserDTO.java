package chatting.chat.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddUserDTO {
    private String userId;
    private String userPw;
    private String email;
    private String userName;

    public RequestAddUserDTO() {
    }

    public RequestAddUserDTO(String userId, String userPw, String email, String userName) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
    }
}
