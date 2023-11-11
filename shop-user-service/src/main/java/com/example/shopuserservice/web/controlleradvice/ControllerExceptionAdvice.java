package com.example.shopuserservice.web.controlleradvice;



import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.trace("[Filter custom error] : " + e);
        return ErrorResponse.toResponseEntity(CustomException.ExceptionToErrorCode(e));
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handles(MissingRequestHeaderException e){
        log.trace("[Filter MissingRequestHeaderException error] : {}",e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.BAD_AUTHORIZATION);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(ConstraintViolationException e){
        log.trace("[Filter ConstraintViolationException error] : {}", e.getConstraintViolations());
        return ErrorResponse.toResponseEntity(ErrorCode.BAD_AUTHORIZATION);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> defaultHandler(Exception e){
        log.trace("[Filter default error] : {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> tokenExceptionHandler(BadCredentialsException e){
        log.trace("[Filter authentication error] : {}",e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.BAD_LOGIN_ID_PW);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> defaultHandlerRunTime(RuntimeException e){
        log.trace("[Filter RuntimeException error] : {}",e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> methodHandler(HttpRequestMethodNotSupportedException e){
        log.trace("[Filter MethodError] : {}",e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.BAD_METHOD);
    }
}
