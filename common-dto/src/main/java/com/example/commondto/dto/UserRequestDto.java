package com.example.commondto.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String userId;
    private String userName;
    private String email;

    public UserRequestDto(String userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }
}
