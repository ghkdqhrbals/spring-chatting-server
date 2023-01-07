package chatting.chat.web.kafka.dto;


import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class RequestAddChatRoomDTO {
    private String userId;
    private List<String> friendIds;

    public RequestAddChatRoomDTO(String userId, Boolean join) {
        this.userId = userId;
    }
}
