package com.example.shopuserservice.web.security;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public class LoginResponseDto {
    private String token;

}
