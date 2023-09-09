package com.example.shopuserservice.domain.data;


import com.example.commondto.format.DateFormat;
import com.example.shopuserservice.domain.data.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class User extends BaseTime {
    @Builder
    public User(String userId, String userPw, String email, String userName, LocalDateTime loginDate, LocalDateTime logoutDate, String role) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.role = role;
    }

    @Id
    @Column(name = "USER_ID", unique = true)
    private String userId;

    @Column(name = "USER_PW")
    private String userPw;

    @Column(name="EMAIL")
    private String email;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name="LOGIN_DATE", columnDefinition = "timestamp(6)")
    private LocalDateTime loginDate;

    @Column(name="LOGOUT_DATE", columnDefinition = "timestamp(6)")
    private LocalDateTime logoutDate;

    @Column(name="ROLE")
    private String role;

}
