package chatting.chat.domain.data;


import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "FRIEND_ID")
    private String friendId;

    public Friend() {}

    public Friend(User user, String friendId) {
        this.user = user;
        this.friendId = friendId;
    }
}
