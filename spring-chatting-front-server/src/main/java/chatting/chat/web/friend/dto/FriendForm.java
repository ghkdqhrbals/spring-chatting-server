package chatting.chat.web.friend.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FriendForm {

    @NotBlank(message = "{friend.add.notEmpty}")
    private String friendId;
}
