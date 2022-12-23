package chatting.chat.web.friend;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class FriendForm {

    @NotEmpty
    private String friendId;
}
