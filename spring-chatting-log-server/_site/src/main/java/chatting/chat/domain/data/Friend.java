package chatting.chat.domain.data;

import chatting.chat.domain.compositekey.FriendId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
//@IdClass(FriendId.class)
public class Friend {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "FRIEND_SEQ")
    private Long id;

    // FK
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "FRIEND_ID")
    private String friendId;

    public Friend() {}
}
