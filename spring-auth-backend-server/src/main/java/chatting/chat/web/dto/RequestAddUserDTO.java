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
}
