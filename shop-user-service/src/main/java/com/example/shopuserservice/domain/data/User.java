package com.example.shopuserservice.domain.data;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    public User(String userId, String userPw, String email, String userName, LocalDateTime joinDate, LocalDateTime loginDate, LocalDateTime logoutDate, String role) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
        this.joinDate = joinDate;
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

    @Column(name="JOIN_DATE")
    private LocalDateTime joinDate;

    @Column(name="LOGIN_DATE")
    private LocalDateTime loginDate;

    @Column(name="LOGOUT_DATE")
    private LocalDateTime logoutDate;

    @Column(name="ROLE")
    private String role;

}
