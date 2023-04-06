package com.example.shopuserservice.web.security;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
