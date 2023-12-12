package chatting.chat.web.dto;

import chatting.chat.web.kafka.dto.CreateChatRoomUnitDTO;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RoomCreationDTO {
    @NotEmpty(message = "친구 목록은 비어 있을 수 없습니다.")
    private List<CreateChatRoomUnitDTO> friends;
    public RoomCreationDTO(){
        this.friends = new ArrayList<>();
    }
    public void addFriend(CreateChatRoomUnitDTO friend){
        this.friends.add(friend);
    }

}
