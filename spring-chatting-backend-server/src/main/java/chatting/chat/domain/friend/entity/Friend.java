package chatting.chat.domain.friend.entity;


import chatting.chat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
public class Friend {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "FRIEND_SEQ")
    private Long id;

    // FK
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "FRIEND_ID")
    private String friendId;

    public Friend() {}

    @Builder
    public Friend(User user, String friendId) {
        this.user = user;
        this.friendId = friendId;
    }
}
