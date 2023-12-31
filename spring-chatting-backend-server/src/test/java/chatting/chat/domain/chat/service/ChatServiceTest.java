package chatting.chat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTO;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "spring.datasource.url=jdbc:tc:postgresql:11-alpine:///databasename" }
)
@ActiveProfiles("test")
class ChatServiceTest extends Initializer {

    private final User testUser = User.builder()
        .userId("userId")
        .userName("userName")
        .userStatus("").build();

    private final User testFriendUser = User.builder()
        .userId("friendId")
        .userName("friendName")
        .userStatus("").build();

    @BeforeEach
    void setUpRemove() {
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방 참여 유저가 채팅 전송 시 성공 반환")
    void whenFindByRoomIdWithValidParticipant_then() {
        // given
        User savedUser = userService.save(testUser.getUserId(), testUser.getUserName(),
            testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        // when
        ChatRecordDTO savedChat = chatService.save(Chatting.builder()
                .id("id")
            .room(roomRepository.findById(roomDto.getRoomId()).get())
            .sendUser(savedUser)
            .message("message")
            .createdAt(LocalDateTime.now())
            .build());

        log.info("savedChat: {}", savedChat);

        // then
        assertThat(savedChat.getMessage()).isEqualTo("message");
        assertThat(savedChat.getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(savedChat.getSendUserId()).isEqualTo(testUser.getUserId());
        assertThat(savedChat.getSendUserName()).isEqualTo(testUser.getUserName());
    }

    @Test
    @DisplayName("참여자가 아닌 유저가 채팅전송 시 에러 반환")
    void whenFindByRoomIdWithInvalidParticipant_thenThrowException() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        userService.save("InvalidParticipantId", "InvalidParticipantName", "");

        // when then
        UserContext.setUserId("InvalidParticipantId"); // ThreadLocal 에 userId 저장

        CustomException customException = assertThrows(CustomException.class, () -> {
            chatService.save(Chatting.builder()
                .room(roomRepository.findById(roomDto.getRoomId()).get())
                .sendUser(userRepository.findById("InvalidParticipantId").get())
                .message("message")
                .createdAt(LocalDateTime.now())
                .build());
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANT);
    }
}