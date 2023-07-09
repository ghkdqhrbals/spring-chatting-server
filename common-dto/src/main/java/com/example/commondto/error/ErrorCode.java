package com.example.commondto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    BAD_EMAIL(BAD_REQUEST,"메일주소가 존재하지 않습니다"),
    TIME_EXPIRED(BAD_REQUEST,"시간 초과하였습니다. 5분 내로 입력해주세요"),
    BAD_CODE(BAD_REQUEST,"잘못된 코드입니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_PASSWORD(UNAUTHORIZED, "잘못된 비밀번호입니다"),
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ALREADY_DELETED_USER(NOT_FOUND, "이미 삭제된 유저입니다"),
    CANNOT_FIND_USER(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    CANNOT_FIND_CODE(NOT_FOUND,"이메일로 확인코드를 먼저 전송해주세요"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),

    /* 500 INTERNAL_SERVER_ERROR : 기본 서버 에러 */
    SERVER_EMAIL_ERROR(INTERNAL_SERVER_ERROR,"서버의 메일발신 기능이 작동하지 않습니다"),
    SERVER_ERROR(INTERNAL_SERVER_ERROR,"서버가 정상적으로 동작하지 않습니다"),





    ;
    private final HttpStatus httpStatus;
    private final String detail;
}