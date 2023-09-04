package com.example.shopuserservice.web.dto;

import com.example.shopuserservice.web.vo.ResponseOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private String userId;
    private String userPw;
    private String email;
    private String userName;
    private LocalDateTime joinDate;
    private LocalDateTime loginDate;
    private LocalDateTime logoutDate;
    private List<ResponseOrder> orders;

    @Builder
    public UserDto(String userId, String userPw, String email, String userName, LocalDateTime joinDate, LocalDateTime loginDate, LocalDateTime logoutDate, List<ResponseOrder> orders) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.userName = userName;
        this.joinDate = joinDate;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.orders = orders;
    }
}
