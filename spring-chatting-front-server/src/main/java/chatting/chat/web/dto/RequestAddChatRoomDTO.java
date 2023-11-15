package chatting.chat.web.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddChatRoomDTO {
    @JsonIgnore
    private String userId;
    private List<String> friendIds;

    public RequestAddChatRoomDTO(List<String> friendIds) {
        this.userId = userId;
        this.friendIds = friendIds;
    }
}
