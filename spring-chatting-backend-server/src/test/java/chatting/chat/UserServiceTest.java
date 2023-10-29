package chatting.chat;


import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.friend.service.FriendServiceImpl;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.kafka.dto.RequestChangeUserStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static chatting.chat.web.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private FriendRepository friendRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @InjectMocks
    private RoomServiceImpl roomService;
    @InjectMocks
    private FriendServiceImpl friendService;

    @Test
    @DisplayName("유저/저장")
    public void w() {
        //pre
        User user = new User("a","a","");

        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.ofNullable(null));
        when(userRepository.save(any(User.class))).thenReturn(user);

        //logic
        User savedUser = userService.save("a", "a", "");

        //validate
        assertThat(savedUser.getUserId()).isEqualTo("a");
        assertThat(savedUser.getUserName()).isEqualTo("a");
        assertThat(savedUser.getUserStatus()).isEqualTo("");
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    @DisplayName("유저/저장 시 중복된 id")
    public void addUser() {
        //pre
        User user = new User("a","a","");
        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.of(user));

        try{
            //logic
            userService.save("a", "a", "");
        }catch(CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(DUPLICATE_USER_RESOURCE);
        }
    }

    @Test
    @DisplayName("유저/확인 시 유저 미발견")
    public void getUser() {
        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.ofNullable(null));
        try{
            //logic
            userService.findById("a");
        }catch(CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("유저/업데이트 상태메세지")
    public void updateUserStatus() {
        //pre
        User user = new User("a","a","");
        RequestChangeUserStatusDTO dto = new RequestChangeUserStatusDTO();
        dto.setUserId("a");
        dto.setStatus("hi");

        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.of(user));

        //logic
        userService.updateUserStatus(dto);

        //validate
        assertThat(user.getUserStatus()).isEqualTo("hi");
    }

    @Test
    @DisplayName("유저/업데이트 상태메세지 시 유저 미발견")
    public void q() {
        //pre
        User user = new User("a","a","");
        RequestChangeUserStatusDTO dto = new RequestChangeUserStatusDTO();
        dto.setUserId("a");
        dto.setStatus("hi");

        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.ofNullable(null));

        try{
            //logic
            userService.updateUserStatus(dto);
        }catch(CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("유저/제거 시 유저 미발견")
    public void a() {
        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.ofNullable(null));

        try{
            //logic
            userService.remove("a");
        }catch(CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("유저/제거")
    public void b() {
        //pre
        User user = new User("a","a","");

        //mocking
        when(userRepository.findById("a")).thenReturn(Optional.of(user));

        //logic
        userService.remove("a");

        //validate
        verify(userRepository).deleteById(any());
    }

    @Test
    @DisplayName("친구/생성")
    public void e() {
        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        when(userRepository.findById("a"))
                .thenReturn(Optional.ofNullable(user_a));
        when(userRepository.findById("b"))
                .thenReturn(Optional.ofNullable(user_b));

        try{
            friendService.save("a","b");
        }catch(CustomException e){
            logErrorInformation(e);
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("친구/생성 시 이미 내가 상대방의 친구")
    public void f() {
        //pre
        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        when(friendRepository.findByUserIdAndFriendId("a","b"))
                .thenReturn(null);
        when(friendRepository.findByUserIdAndFriendId("b","a"))
                .thenReturn(new Friend(user_b,"a"));

        //logic
        friendService.save("a","b");
    }

    @Test
    @DisplayName("친구/생성 시 이미 내가 상대방과 친구")
    public void c() {
        //pre
        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        when(friendRepository.findByUserIdAndFriendId("a","b"))
                .thenReturn(new Friend(user_a,"b"));
        when(friendRepository.findByUserIdAndFriendId("b","a"))
                .thenReturn(null);

        //logic
        try{
            friendService.save("a","b");
        }catch(CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(DUPLICATE_FRIEND);
        }
    }

    @Test
    @DisplayName("친구/생성 시 친구유저 미발견")
    public void d() {
        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));

        try{
            friendService.save("a","b");
        }catch(CustomException e){
            logErrorInformation(e);
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("친구/생성 시 스스로에게 친구요청")
    public void v() {
        //pre
        User user_a = new User("a","a","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a));

        //logic
        try{
            friendService.save("a","a");
        }catch(CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(DUPLICATE_FRIEND_SELF);
        }
    }


    @Test
    @DisplayName("방/생성")
    public void r() {
        Long expectedId = 1L;
        Room room = new Room(ZonedDateTime.now(),ZonedDateTime.now());

        doAnswer(invocation -> {
            ReflectionTestUtils.setField((Room) invocation.getArgument(0), "roomId", expectedId);
            return null;
        }).when(roomRepository).save(any(Room.class));

        roomRepository.save(room);
        assertThat(room.getRoomId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("방/확인")
    public void t() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        //mocking
        when(roomRepository.findById(1L)).thenReturn(Optional.ofNullable(room));

        //logic
        Room findRoom = roomService.findByRoomId(1L);

        //validate
        assertThat(findRoom.getCreatedAt()).isEqualTo(createdAt);
        assertThat(findRoom.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("방/확인 시 방 미발견")
    public void u() {
        //mocking
        when(roomRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        try {
            //logic
            roomService.findByRoomId(1L);
        }catch (CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_ROOM);
        }
    }

    @Test
    @DisplayName("방/제거 시 방 미발견")
    public void i() {
        //mocking
        when(roomRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        try {
            //logic
            roomService.remove(1L);
        }catch (CustomException e){
            logErrorInformation(e);
            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_ROOM);
        }
    }
    

    private static void logErrorInformation(CustomException e) {
        log.info("Exception = {}, Details = {}", e.getErrorCode(), e.getErrorCode().getDetail());
    }

    private void mockUserRepositoryFindById(List<User> user){
        when(userRepository.findById(any()))
                .thenAnswer(input -> {
                    var arg1 = input.getArgument(0);
                    for (User u : user){
                        if (u.getUserId().equals(arg1)){
                            return Optional.ofNullable(u);
                        }
                    }
                    return Optional.ofNullable(null);
                });
    }
}