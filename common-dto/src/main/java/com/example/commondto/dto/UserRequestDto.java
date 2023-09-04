package com.example.commondto.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestDto {
    private String userId;
    private String userName;
    private String email;

    @Builder
    public UserRequestDto(String userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }
}
