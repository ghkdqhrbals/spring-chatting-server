package com.example.commondto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException implements Serializable { // RuntimeException 상속으로 Transactional Rollback
    private final ErrorCode errorCode;

    public static ErrorCode ExceptionToErrorCode(Throwable e) {
        CustomException customException = (CustomException) e;
        return customException.getErrorCode();
    }

}