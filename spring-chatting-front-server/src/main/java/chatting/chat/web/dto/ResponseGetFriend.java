package chatting.chat.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetFriend {
    private String friendId;
    private String friendName;
    private String friendStatus;
}