package chatting.chat.domain.participant.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
class ParticipantServiceImplTest extends Initializer {
    private final User testUser = User.builder()
        .userId("userId")
        .userName("userName")
        .userStatus("").build();

    private final User testFriendUser = User.builder()
        .userId("friendId")
        .userName("friendName")
        .userStatus("").build();

    private final User testNewUser = User.builder()
        .userId("newUserId")
        .userName("newUserName")
        .userStatus("").build();

    @BeforeEach
    void setUpRemove() {
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }



    @Test
    @DisplayName("유저 아이디로 현재 참여중인 채팅방 조회 성공")
    void findAllByUserId() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        assertThat(roomDto.getUsers()).hasSize(2);
        assertThat(roomDto.getUsers().get(0).getUserId()).isEqualTo(testUser.getUserId());
        assertThat(roomDto.getUsers().get(1).getUserId()).isEqualTo(testFriendUser.getUserId());

        // when
        List<ParticipantDto> allByUserId = participantService.findAllByUserId(testUser.getUserId());

        // then
        allByUserId.forEach(participantDto -> log.info("participantDto: {}", participantDto));

        assertThat(allByUserId).hasSize(1);
        assertThat(allByUserId.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(allByUserId.get(0).getUserDto().getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    @DisplayName("채팅방 아이디로 참여중인 유저 조회 성공")
    void whenFindParticipantWithValidRoomId_thenAllParticipantsShouldBeReturned() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        assertThat(roomDto.getUsers()).hasSize(2);
        assertThat(roomDto.getUsers().get(0).getUserId()).isEqualTo(testUser.getUserId());
        assertThat(roomDto.getUsers().get(1).getUserId()).isEqualTo(testFriendUser.getUserId());

        // when
        List<ParticipantDto> participants = participantService.findParticipantByRoomId(roomDto.getRoomId());

        // then
        participants.forEach(participantDto -> log.info("participantDto: {}", participantDto));
        assertThat(participants).hasSize(2);
        assertThat(participants.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(0).getUserDto().getUserId()).isEqualTo(testUser.getUserId());
        assertThat(participants.get(1).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(1).getUserDto().getUserId()).isEqualTo(testFriendUser.getUserId());
    }

    @Test
    @DisplayName("방 생성 시 해당 참여자와 친구가 아니라면 에러 반환")
    void whenParicipantUserIsNotValid_thenCustomExceptionShouldBeReturned() throws CustomException{
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // when then
        CustomException customException = assertThrows(CustomException.class, () -> {
            userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
                .userId(testUser.getUserId())
                .friendIds(Arrays.asList(testFriendUser.getUserId()))
                .build());
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_FIND_FRIEND);
    }

    @Test
    @DisplayName("채팅방 참여 시 채팅방에 존재하지 않으면 에러 반환")
    void whenRoomIsNotValid_thenCustomExceptionShouldBeReturned() throws CustomException{
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // when then
        CustomException customException = assertThrows(CustomException.class, () -> {
            participantService.addParticipant(1L, testUser.getUserId());
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_FIND_ROOM);
    }

    @Test
    @DisplayName("채팅방에 참여 성공")
    void whenValidParticipantAdd_thenSuccessShouldBeReturned() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        userService.save(testNewUser.getUserId(), testNewUser.getUserName(), testNewUser.getUserStatus());
        UserContext.setUserId(testNewUser.getUserId()); // ThreadLocal 에 userId 저장

        // when
        String success = participantService.addParticipant(roomDto.getRoomId(), testNewUser.getUserId());

        // then
        List<ParticipantDto> participants = participantService.findParticipantByRoomId(roomDto.getRoomId());

        participants.forEach(participantDto -> log.info("participantDto: {}", participantDto));
        assertThat(participants).hasSize(3);
        assertThat(participants.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(0).getUserDto().getUserId()).isEqualTo(testUser.getUserId());
        assertThat(participants.get(1).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(1).getUserDto().getUserId()).isEqualTo(testFriendUser.getUserId());
        assertThat(participants.get(2).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(2).getUserDto().getUserId()).isEqualTo(testNewUser.getUserId());
    }

    @Test
    @DisplayName("채팅방에서 나가기 성공")
    void whenValidParticipantRemove_thenSuccessShouldBeReturned() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        userService.save(testNewUser.getUserId(), testNewUser.getUserName(), testNewUser.getUserStatus());
        UserContext.setUserId(testNewUser.getUserId()); // ThreadLocal 에 userId 저장

        // 채팅방 참여
        participantService.addParticipant(roomDto.getRoomId(), testNewUser.getUserId());

        // when
        String success = participantService.remove(roomDto.getRoomId(), testNewUser.getUserId());

        // then
        List<ParticipantDto> participants = participantService.findParticipantByRoomId(roomDto.getRoomId());

        participants.forEach(participantDto -> log.info("participantDto: {}", participantDto));
        assertThat(participants).hasSize(2);
        assertThat(participants.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(0).getUserDto().getUserId()).isEqualTo(testUser.getUserId());
        assertThat(participants.get(1).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(1).getUserDto().getUserId()).isEqualTo(testFriendUser.getUserId());
    }

    @Test
    @DisplayName("채팅방에 참여중인 유저 목록 조회 성공")
    void whenValidParticipantGet_thenSuccessShouldBeReturned() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        userService.save(testNewUser.getUserId(), testNewUser.getUserName(), testNewUser.getUserStatus());
        UserContext.setUserId(testNewUser.getUserId()); // ThreadLocal 에 userId 저장

        // 채팅방 참여
        participantService.addParticipant(roomDto.getRoomId(), testNewUser.getUserId());

        // when
        List<ParticipantDto> participants = participantService.findParticipantByRoomId(roomDto.getRoomId());

        // then
        participants.forEach(participantDto -> log.info("participantDto: {}", participantDto));
        assertThat(participants).hasSize(3);
        assertThat(participants.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(0).getUserDto().getUserId()).isEqualTo(testUser.getUserId());
        assertThat(participants.get(1).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(1).getUserDto().getUserId()).isEqualTo(testFriendUser.getUserId());
        assertThat(participants.get(2).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(2).getUserDto().getUserId()).isEqualTo(testNewUser.getUserId());
    }

    @Test
    @DisplayName("기존 채팅방 참여 시 내 채팅방 목록 조회 성공")
    void whenValidParticipantGetMyRoom_thenSuccessShouldBeReturned() {
        // given
        // 유저 추가
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        UserContext.setUserId(testUser.getUserId()); // ThreadLocal 에 userId 저장

        // 친구 추가
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // 채팅방 생성
        RoomDto roomDto = userService.makeRoomWithFriends(RequestAddChatRoomDTO.builder()
            .userId(testUser.getUserId())
            .friendIds(Arrays.asList(testFriendUser.getUserId()))
            .build());

        userService.save(testNewUser.getUserId(), testNewUser.getUserName(), testNewUser.getUserStatus());
        UserContext.setUserId(testNewUser.getUserId()); // ThreadLocal 에 userId 저장

        // 채팅방 참여
        participantService.addParticipant(roomDto.getRoomId(), testNewUser.getUserId());

        // when
        List<ParticipantDto> participants = participantService.findAllByUserId(testNewUser.getUserId());

        // then
        participants.forEach(participantDto -> log.info("participantDto: {}", participantDto));
        assertThat(participants).hasSize(1);
        assertThat(participants.get(0).getRoomId()).isEqualTo(roomDto.getRoomId());
        assertThat(participants.get(0).getUserDto().getUserId()).isEqualTo(testNewUser.getUserId());
    }


}