package chatting.chat.web.kafka.dto;

import chatting.chat.domain.data.Friend;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseUserFriendDTO {
    private String userId;
    private Boolean isSuccess;
    private String stat;
    private List<Friend> friend;
    public ResponseUserFriendDTO(String userId) {
        this.userId = userId;
        this.isSuccess = false;
        this.stat = "";
        this.friend = new ArrayList<>();
    }
}
