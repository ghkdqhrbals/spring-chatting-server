package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
public class User {
    public User(String userId, String userPw, String email, String userName, LocalDate joinDate, LocalDate loginDate, LocalDate logoutDate) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
        this.joinDate = joinDate;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
    }


    private String userId;
    private String userPw;
    private String email;
    private String userName;
    private LocalDate joinDate;
    private LocalDate loginDate;
    private LocalDate logoutDate;

    public User() {

    }
}
