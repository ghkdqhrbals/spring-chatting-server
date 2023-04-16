package com.example.commondto.dto;

import lombok.Data;

@Data
public class ResponseUserChangeDto {
    private String userId;

    public ResponseUserChangeDto() {
    }

    public ResponseUserChangeDto(String userId) {
        this.userId = userId;
    }
}
