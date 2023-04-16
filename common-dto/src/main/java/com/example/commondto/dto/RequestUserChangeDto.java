package com.example.commondto.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestUserChangeDto {
    private String userId;

    public RequestUserChangeDto(String userId) {
        this.userId = userId;
    }
}
