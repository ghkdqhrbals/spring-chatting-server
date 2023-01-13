package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
public class User {
    public User(String userId, String userName, String userStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userStatus = userStatus;
    }
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "USER_STATUS")
    private String userStatus;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "sendUser", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Chatting> chattings = new ArrayList<>();

    public User() {

    }
}
