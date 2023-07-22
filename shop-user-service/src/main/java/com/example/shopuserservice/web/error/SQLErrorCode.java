package com.example.shopuserservice.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SQLErrorCode {
    unique_violation("23505"),
    ;
    private String SQLStateCode;

}
