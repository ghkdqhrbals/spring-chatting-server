package chatting.chat.domain.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class Friend {

    private Long id;
    private User user;
    private String friendId;

}
