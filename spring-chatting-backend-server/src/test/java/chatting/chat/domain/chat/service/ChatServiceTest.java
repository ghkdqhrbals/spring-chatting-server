package chatting.chat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.RequestAddChatMessageDTO;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTO;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
        RequestAddChatMessageDTO requestAddChatMessageDTO = RequestAddChatMessageDTO.builder()
            .roomId(roomDto.getRoomId())
            .message("message")
            .build();

        System.out.println("Test Start");
        // when
        ChatRecordDTO savedChat = chatService.save(requestAddChatMessageDTO, savedUser.getUserId());

        log.info("savedChat: {}", savedChat);

        // then
        assertThat(savedChat.getMessage()).isEqualTo("message");
        assertThat(savedChat.getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(savedChat.getSendUserId()).isEqualTo(testUser.getUserId());
        assertThat(savedChat.getSendUserName()).isEqualTo(testUser.getUserName());
    }

    @Test
    @DisplayName("10개의 채팅 전송 시 성공 반환과 ascending 순서 확인 성공")
    void whenAddMultipleChat_thenSuccess() {
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

        for (int i = 0; i < 10; i++) {
            RequestAddChatMessageDTO requestAddChatMessageDTO = RequestAddChatMessageDTO.builder()
                .roomId(roomDto.getRoomId())
                .message("message"+String.valueOf(i))
                .build();
            // when
            ChatRecordDTO savedChat = chatService.save(requestAddChatMessageDTO, savedUser.getUserId());
            // then
            assertThat(savedChat.getMessage()).isEqualTo("message"+String.valueOf(i));
            assertThat(savedChat.getRoomId()).isEqualTo(roomDto.getRoomId());
            assertThat(savedChat.getSendUserId()).isEqualTo(testUser.getUserId());
            assertThat(savedChat.getSendUserName()).isEqualTo(testUser.getUserName());
        }

        System.out.println("----------------------");
        List<ChatRecordDTO> allChatRecords = chatService.findAllByRoomId(roomDto.getRoomId());
        int size = allChatRecords.size();
        assertThat(size).isEqualTo(10);

        AtomicInteger i = new AtomicInteger(0);

        allChatRecords.forEach(chatRecordDTO -> {
            String s = "message" + String.valueOf(i.getAndIncrement());
            assertThat(chatRecordDTO.getMessage()).isEqualTo(s);
        });
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

        RequestAddChatMessageDTO requestAddChatMessageDTO = RequestAddChatMessageDTO.builder()
            .roomId(roomDto.getRoomId())
            .message("message")
            .build();

        // when then
        UserContext.setUserId("InvalidParticipantId"); // ThreadLocal 에 userId 저장

        CustomException customException = assertThrows(CustomException.class, () -> {
            chatService.save(requestAddChatMessageDTO, "InvalidParticipantId");
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANT);
    }
}