package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserChange {
    private String userId;
    private String userName;
    private String userStatus;

    private String insertOrDelete;

    public RequestUserChange(String userId, String userName, String userStatus, String insertOrDelete) {
        this.userId = userId;
        this.userName = userName;
        this.userStatus = userStatus;
        this.insertOrDelete = insertOrDelete;
    }

    public RequestUserChange() {
    }
}
