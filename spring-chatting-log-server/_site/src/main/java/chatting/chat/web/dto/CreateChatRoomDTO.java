package chatting.chat.web.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomDTO {
    private String friendId;
    private Boolean join;

    public CreateChatRoomDTO(String friendId, Boolean join) {
        this.friendId = friendId;
        this.join = join;
    }

    public CreateChatRoomDTO() {
        this.join = false;
    }
}
