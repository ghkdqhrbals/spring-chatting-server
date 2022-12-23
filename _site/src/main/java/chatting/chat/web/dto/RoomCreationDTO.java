package chatting.chat.web.dto;

import chatting.chat.domain.data.Friend;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RoomCreationDTO {
    private List<CreateChatRoomDTO> friends;
    public RoomCreationDTO(){
        this.friends = new ArrayList<>();
    }

    public void addFriend(CreateChatRoomDTO friend){
        this.friends.add(friend);
    }

}
