package com.example.commondto.error;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    ALREADY_FRIEND(400,"server.friend.exist" ),
    DUPLICATE_PARTICIPANT(400,"participant.duplicate" ),
    CANNOT_ADD_SELF(400,"server.user.self.add" ),
    INVALID_PARTICIPANT(400, "participant.notFound"),
    BAD_REQUEST_DEFAULT(400,"BAD REQUEST"),
    INVALID_TOKEN(400, "server.user.invalidToken"),
    BAD_EMAIL(400,"메일주소가 존재하지 않습니다"),
    BAD_USER_TYPE(400,"유저 타입을 설정해주세요"),
    BAD_METHOD(400,"해당 HTTP 메소드를 지원하지 않습니다"),
    BAD_AUTHORIZATION(400,"인증 헤더가 존재하지 않습니다"),
    BAD_CODE(400,"잘못된 코드입니다"),

    EXPIRED_TIME(400,"시간 초과하였습니다. 5분 내로 입력해주세요"),
    EXPIRED_TOKEN(400,"토큰이 만료되었습니다"),
    UNSUPPORTED_TOKEN(400,"지원하지 않는 토큰입니다"),
    EMPTY_TOKEN(400,"토큰이 비어있습니다"),
    MANIPULATED_PAYMENT_AMOUNT(400,"주문금액과 실 결제금액이 다릅니다"),
    BAD_AMOUNT(400,"해당 라이센스의 잔여사용횟수가 없습니다"),
    BAD_VERIFICATION_STATUS(400,"이메일 인증에 실패하였습니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    BAD_LOGIN_ID_PW(401, "자격 증명에 실패하였습니다"),
    INVALID_CREDENTIAL(401, "server.user.invalidCredential"),
    INVALID_PASSWORD(401, "잘못된 비밀번호입니다"),


    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_USER(403, "server.user.forbidden"),
    FORBIDDEN_AUTH_TOKEN(403,"권한정보가 토큰에 존재하지 않습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ALREADY_DELETED_USER(404, "이미 삭제된 유저입니다"),
    CANNOT_FIND_USER(404, "server.user.notFound"),
    CANNOT_FIND_CODE(404,"이메일로 확인코드를 먼저 전송해주세요"),
    CANNOT_FIND_ROOM(404, "해당 채팅방 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND_USER(404, "해당 친구 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND(404, "해당 유저와 친구가 아닙니다"),
    CANNOT_FIND_PARTICIPANT(404, "participant.notFound"),
    CANNOT_FIND_CHATTING(404, "해당 채팅 정보를 찾을 수 없습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(409, "데이터가 이미 존재합니다"),

    /* 500 INTERNAL_SERVER_ERROR : 기본 서버 에러 */
    SERVER_EMAIL_ERROR(500,"서버의 메일발신 기능이 작동하지 않습니다"),
    SERVER_ERROR(500,"서버가 정상적으로 동작하지 않습니다"),

    /* 503 SERVICE_UNAVAILABLE : 서비스 이용 불가 */
    SERVICE_UNAVAILABLE(503, "server.service.notFound")



    ;


    private final int httpStatus;
    private final String detail;


    @Override
    public String toString() {
        return "ErrorCode= " + this.name() + ", detail= " + detail;
    }

}
