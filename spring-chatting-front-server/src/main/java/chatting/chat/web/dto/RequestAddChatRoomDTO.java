package chatting.chat.web.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddChatRoomDTO {
    private String userId;
    private List<String> friendIds;

    public RequestAddChatRoomDTO(String userId, List<String> friendIds) {
        this.userId = userId;
        this.friendIds = friendIds;
    }
}
