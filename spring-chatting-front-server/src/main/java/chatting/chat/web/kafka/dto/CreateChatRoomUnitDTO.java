package chatting.chat.web.kafka.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomUnitDTO {
    private String userId;
    private String userName;
    private Boolean join;

    public CreateChatRoomUnitDTO(String userId, String userName, Boolean join) {
        this.userId = userId;
        this.userName = userName;
        this.join = join;
    }

    public CreateChatRoomUnitDTO() {
        this.join = false;
    }
}
