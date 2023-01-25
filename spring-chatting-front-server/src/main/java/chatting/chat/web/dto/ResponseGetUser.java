package chatting.chat.web.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetUser {
    private String userId;
    private String userName;
    private String userStatus;

    public ResponseGetUser() {
    }

    public ResponseGetUser(String userId, String userName, String userStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userStatus = userStatus;
    }
}
