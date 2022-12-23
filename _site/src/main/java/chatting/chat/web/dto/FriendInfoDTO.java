package chatting.chat.web.dto;


import lombok.Data;

@Data
public class FriendInfoDTO {
    String friendId;
    String friendName;
    String friendStatusMessage;

    public FriendInfoDTO(String friendId, String friendName, String friendStatusMessage) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendStatusMessage = friendStatusMessage;
    }
}
