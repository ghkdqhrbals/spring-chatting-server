package chatting.chat.web.kafka.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddChatRoomDTO {
    private String userId;
    private Boolean join;

    public RequestAddChatRoomDTO(String userId, Boolean join) {
        this.userId = userId;
        this.join = join;
    }

    public RequestAddChatRoomDTO() {
        this.join = false;
    }
}
