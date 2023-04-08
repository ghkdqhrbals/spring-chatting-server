package com.example.shopuserservice.web.security;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class LoginResponseDto {
    private String token;
}
