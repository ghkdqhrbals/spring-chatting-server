package com.example.shopuserservice.web.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestRole {
    @NotNull
    @Size(min = 4, message = "Length of roleName should be more than 4")
    private String roleName;
}
