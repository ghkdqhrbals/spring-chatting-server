package chatting.chat.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_TOKEN(BAD_REQUEST, "server.user.invalidToken"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_CREDENTIAL(UNAUTHORIZED, "server.user.invalidCredential"),

    /* 403 UNAUTHORIZED : 접근권한제한 사용자 */
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "server.user.forbidden"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    CANNOT_FIND_USER(NOT_FOUND, "server.user.notFound"),
    CANNOT_FIND_ROOM(NOT_FOUND, "해당 채팅방 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND_USER(NOT_FOUND, "해당 친구 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND(NOT_FOUND, "해당 유저와 친구가 아닙니다"),
    CANNOT_FIND_CHATTING(NOT_FOUND, "해당 채팅 정보를 찾을 수 없습니다"),

//    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),

    // 503 SERVICE_UNAVAILABLE : 서비스 이용 불가
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "server.service.notFound"),

    ;

    private final HttpStatus httpStatus;
    private final String detail;
}