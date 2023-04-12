package com.example.commondto.dto;

import lombok.Data;

@Data
public class ResponseUserChangeDto {
    private String userId;
    private String userName;

    public ResponseUserChangeDto() {
    }

    public ResponseUserChangeDto(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
