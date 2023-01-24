package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestAddFriendDTO {
    private String userId;
    private List<String> friendId;

    public RequestAddFriendDTO() {
    }

    public RequestAddFriendDTO(String userId, List<String> friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
