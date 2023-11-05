package com.example.commondto.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String code;
    private String message;

    @Builder
    @JsonCreator
    public ErrorResponse(
        @JsonProperty("errorCode") String code,
        @JsonProperty("errorMessage") String message,
        @JsonProperty("status") int status){
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus())
                        .code(errorCode.name())
                        .message(errorCode.getDetail())
                        .build()
                );
    }
}