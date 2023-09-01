package com.example.shopuserservice.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException { // RuntimeException 상속으로 Transactional Rollback
    private final ErrorCode errorCode;


    public static ErrorCode ExceptionToErrorCode(Throwable e) {
        CustomException customException = (CustomException) e;
        return customException.getErrorCode();
    }

    @Override
    public String toString() {
        return "CustomException= {" +errorCode+ '}';
    }
}