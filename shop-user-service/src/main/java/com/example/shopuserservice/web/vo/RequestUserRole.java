package com.example.shopuserservice.web.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUserRole {

    @NotNull(message = "user id must not be null")
    private String userId;

    @NotNull(message = "role name must not be null")
    private String roleName;
}
