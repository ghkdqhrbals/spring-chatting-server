package com.example.shopuserservice.web.dto;

import com.example.shopuserservice.web.vo.ResponseOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class UserDto {
    private String userId;
    private String userPw;
    private String email;
    private String userName;
    private LocalDateTime joinDate;
    private LocalDateTime loginDate;
    private LocalDateTime logoutDate;
    private List<ResponseOrder> orders;
}
