package chatting.chat.domain.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
public class User {
    public User(String userId, String userPw, String email, String userName, String userStatus, LocalDate joinDate, LocalDate loginDate, LocalDate logoutDate) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
        this.userStatus = userStatus;
        this.joinDate = joinDate;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
    }

    @Id
    @Column(name = "USER_ID")
    private String userId;


    @Column(name = "USER_PW")
    private String userPw;

    @Column(name="EMAIL")
    private String email;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "USER_STATUS")
    private String userStatus;

    @Column(name="JOIN_DATE")
    private LocalDate joinDate;

    @Column(name="LOGIN_DATE")
    private LocalDate loginDate;

    @Column(name="LOGOUT_DATE")
    private LocalDate logoutDate;

    public User() {

    }
}
