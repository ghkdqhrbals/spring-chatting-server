package chatting.chat.web.dto;

import chatting.chat.web.kafka.dto.CreateChatRoomUnitDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RoomCreationDTO {
    private List<CreateChatRoomUnitDTO> friends;
    public RoomCreationDTO(){
        this.friends = new ArrayList<>();
    }

    public void addFriend(CreateChatRoomUnitDTO friend){
        this.friends.add(friend);
    }

}
