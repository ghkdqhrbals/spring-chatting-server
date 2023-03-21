package chatting.chat.domain.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    public User(String userId, String userPw, String email, String userName, LocalDateTime joinDate, LocalDateTime loginDate, LocalDateTime logoutDate) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
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

    @Column(name="JOIN_DATE")
    private LocalDateTime joinDate;

    @Column(name="LOGIN_DATE")
    private LocalDateTime loginDate;

    @Column(name="LOGOUT_DATE")
    private LocalDateTime logoutDate;

}
