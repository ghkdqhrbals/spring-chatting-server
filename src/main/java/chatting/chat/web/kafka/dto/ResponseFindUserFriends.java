package chatting.chat.web.kafka.dto;


import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseFindUserFriends {
    private User user;
    private List<Friend> friends;
}
