package com.example.shopuserservice.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    BAD_EMAIL(BAD_REQUEST,"메일주소가 존재하지 않습니다"),
    BAD_USER_TYPE(BAD_REQUEST,"유저 타입을 설정해주세요"),
    BAD_METHOD(BAD_REQUEST,"해당 HTTP 메소드를 지원하지 않습니다"),
    BAD_AUTHORIZATION(BAD_REQUEST,"인증 헤더가 존재하지 않습니다"),
    BAD_CODE(BAD_REQUEST,"잘못된 코드입니다"),
    BAD_LOGIN_ID_PW(UNAUTHORIZED, "자격 증명에 실패하였습니다"),
    EXPIRED_TIME(BAD_REQUEST,"시간 초과하였습니다. 5분 내로 입력해주세요"),
    EXPIRED_TOKEN(BAD_REQUEST,"토큰이 만료되었습니다"),
    UNSUPPORTED_TOKEN(BAD_REQUEST,"지원하지 않는 토큰입니다"),
    EMPTY_TOKEN(BAD_REQUEST,"토큰이 비어있습니다"),
    MANIPULATED_PAYMENT_AMOUNT(BAD_REQUEST,"주문금액과 실 결제금액이 다릅니다"),
    BAD_AMOUNT(BAD_REQUEST,"해당 라이센스의 잔여사용횟수가 없습니다"),
    BAD_VERIFICATION_STATUS(BAD_REQUEST,"이메일 인증에 실패하였습니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_PASSWORD(UNAUTHORIZED, "잘못된 비밀번호입니다"),
    INVALID_TOKEN(UNAUTHORIZED, "잘못된 토큰입니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_USER(FORBIDDEN,"해당 URL에 접근하기 위한 권한이 없습니다"),
    FORBIDDEN_AUTH_TOKEN(FORBIDDEN,"권한정보가 토큰에 존재하지 않습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ALREADY_DELETED_USER(NOT_FOUND, "이미 삭제된 유저입니다"),
    CANNOT_FIND_USER(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    CANNOT_FIND_CODE(NOT_FOUND,"이메일로 확인코드를 먼저 전송해주세요"),
    CANNOT_FIND_ROOM(NOT_FOUND, "해당 채팅방 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND_USER(NOT_FOUND, "해당 친구 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND(NOT_FOUND, "해당 유저와 친구가 아닙니다"),
    CANNOT_FIND_CHATTING(NOT_FOUND, "해당 채팅 정보를 찾을 수 없습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),

    /* 500 INTERNAL_SERVER_ERROR : 기본 서버 에러 */
    SERVER_EMAIL_ERROR(INTERNAL_SERVER_ERROR,"서버의 메일발신 기능이 작동하지 않습니다"),
    SERVER_ERROR(INTERNAL_SERVER_ERROR,"서버가 정상적으로 동작하지 않습니다"),

    ;
    private final HttpStatus httpStatus;
    private final String detail;


    @Override
    public String toString() {
        return "ErrorCode= " + this.name() + ", detail= " + detail;
    }

}