package com.example.shopuserservice.utils;


import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ErrorUtils {

    public static CompletableFuture<ResponseEntity<Object>> sendResponseEntityOrError(CompletableFuture<Object> cf) {
        return cf
                .thenApply(ResponseEntity::ok)
                .exceptionally(e->{
                    log.trace("커스텀 에러 타입 변환: CAUSE {}, MESSAGE {}",e.getCause().getClass().getName(), e.getCause().getMessage());
                    // 에러 변환
                    return ErrorResponse.toResponseEntity(CustomException.ExceptionToErrorCode(e.getCause()));
                }).exceptionally(e->{
                    // 기본 에러
                    return ErrorResponse.toResponseEntity(ErrorCode.SERVER_ERROR);
                });
    }

    public static CompletableFuture<ResponseEntity<Object>> sendResponseEntityOrErrorT(CompletableFuture<Object> cf) {
        return cf
                .thenApply(data->{
                    log.trace("DataClass: {}, Data: {}",data.getClass().getName(), data.toString());
                    return ResponseEntity.ok(data);
                }).exceptionally(e->{
                    log.trace("커스텀 에러 타입 변환: CAUSE {}, MESSAGE {}",e.getCause().getClass().getName(), e.getCause().getMessage());
                    // 에러 변환
                    return ErrorResponse.toResponseEntity(CustomException.ExceptionToErrorCode(e.getCause()));
                }).exceptionally(e->{
                    // 기본 에러
                    return ErrorResponse.toResponseEntity(ErrorCode.SERVER_ERROR);
                });
    }
}
