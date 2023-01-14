package chatting.chat.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    DUPLICATE_FRIEND(BAD_REQUEST, "이미 추가한 친구입니다"),
    DUPLICATE_FRIEND_SELF(BAD_REQUEST, "자신을 친구추가 할 수 없습니다"),
    INVALID_PARTICIPANT(BAD_REQUEST, "해당 채팅방에 참여하는 유저가 아닙니다"),
    DUPLICATE_PARTICIPANT(BAD_REQUEST, "해당 채팅방에 이미 참여중입니다"),


    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    CANNOT_FIND_USER(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    CANNOT_FIND_ROOM(NOT_FOUND, "해당 채팅방 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND_USER(NOT_FOUND, "해당 친구 정보를 찾을 수 없습니다"),
    CANNOT_FIND_FRIEND(NOT_FOUND, "해당 유저와 친구가 아닙니다"),
    CANNOT_FIND_CHATTING(NOT_FOUND, "해당 채팅 정보를 찾을 수 없습니다"),
    CANNOT_FIND_PARTICIPANT(NOT_FOUND, "해당 채팅방에 참여하는 유저가 아닙니다"),

//    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    DUPLICATE_USER_RESOURCE(CONFLICT, "유저 데이터가 이미 존재합니다"),
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}