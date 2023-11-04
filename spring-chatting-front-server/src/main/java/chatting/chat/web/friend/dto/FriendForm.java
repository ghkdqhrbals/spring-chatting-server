package chatting.chat.web.friend.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class FriendForm {

    @NotBlank(message = "friendId is mandatory")
    private String friendId;
}
