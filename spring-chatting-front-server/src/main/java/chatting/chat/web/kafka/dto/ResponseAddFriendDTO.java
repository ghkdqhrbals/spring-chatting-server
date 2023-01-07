package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseAddFriendDTO {
    private String userId;
    private List<String> friendId;
    private Boolean isSuccess;
    private String errorMessage;

    public ResponseAddFriendDTO(String userId) {
        this.userId = userId;
        this.isSuccess = false;
        this.errorMessage="";
    }
}
