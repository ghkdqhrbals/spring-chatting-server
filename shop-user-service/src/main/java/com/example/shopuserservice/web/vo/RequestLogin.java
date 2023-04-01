package com.example.shopuserservice.web.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLogin {
    @NotNull(message = "userId should not be null")
    private String userId;

    @NotNull(message = "userPw should not be null")
    @Size(min = 4, message = "userPw should not be less than 4")
    private String userPw;
}
