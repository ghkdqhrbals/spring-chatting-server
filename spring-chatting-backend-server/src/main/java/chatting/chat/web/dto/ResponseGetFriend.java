package chatting.chat.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetFriend {
    private String friendId;
    private String friendName;
    private String friendStatus;

    public ResponseGetFriend() {
    }

    public ResponseGetFriend(String friendId, String friendName, String friendStatus) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendStatus = friendStatus;
    }
}
