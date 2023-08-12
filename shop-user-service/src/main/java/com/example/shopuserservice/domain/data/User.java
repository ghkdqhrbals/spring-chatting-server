package com.example.shopuserservice.domain.data;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    @Builder
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getUserId().equals(user.getUserId()) && getUserPw().equals(user.getUserPw()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getJoinDate(), user.getJoinDate()) && Objects.equals(getLoginDate(), user.getLoginDate()) && Objects.equals(getLogoutDate(), user.getLogoutDate()) && getRole().equals(user.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getUserPw(), getEmail(), getUserName(), getJoinDate(), getLoginDate(), getLogoutDate(), getRole());
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
