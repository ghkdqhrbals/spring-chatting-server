package chatting.chat;

import chatting.chat.domain.chat.service.ChatService;
import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static chatting.chat.web.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MultiServiceTest {
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
    private ChatService chatService;
    @InjectMocks
    private ParticipantServiceImpl participantService;


    @BeforeEach
    public void initialize(){
    }

    @Test
    @DisplayName("방/생성, 참여인원/생성")
    public void a() {
        //pre
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("b"));

        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        Participant participant1 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());
        Participant participant2 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());


        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        mockFriendRepositoryFindByUserIdAndFriendId(user_a, user_b);
        mockRoomRepositorySave(room);

        //logic
        userService.makeRoomWithFriends(request);
    }


    @Test
    @DisplayName("방/생성, 참여인원/생성 시 요청유저 미발견")
    public void b() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        Participant participant1 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());
        Participant participant2 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));

        //logic
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("c");
        request.setFriendIds(Arrays.asList("b"));
        try {
            userService.makeRoomWithFriends(request);
        }catch (CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("방/생성, 참여인원/생성 시 친구유저 미발견")
    public void c() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        Participant participant1 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());
        Participant participant2 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));

        //logic
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("c"));
        try {
            userService.makeRoomWithFriends(request);
        }catch (CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_USER);
        }
    }

    @Test
    @DisplayName("방/생성, 참여인원/생성 시 서로 친구가 아님")
    public void d() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        when(friendRepository.findByUserIdAndFriendId(user_a.getUserId(),user_b.getUserId())).thenReturn(null);

        //logic
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("b"));
        try {
            userService.makeRoomWithFriends(request);
        }catch (CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_FRIEND);
        }
    }

    @Test
    @DisplayName("방/생성, 참여인원/생성 시 상대방이 나와 친구가 아님")
    public void f() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        when(friendRepository.findByUserIdAndFriendId(user_a.getUserId(),user_b.getUserId()))
                .thenReturn(new Friend(user_a,user_b.getUserId()));
        when(friendRepository.findByUserIdAndFriendId(user_b.getUserId(),user_a.getUserId()))
                .thenReturn(null);

        //logic
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("b"));

        try {
            userService.makeRoomWithFriends(request);
        }catch (CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_FRIEND);
        }
    }


    @Test
    @DisplayName("방/생성, 참여인원/생성 시 내가 상대방과 친구가 아님")
    public void g() {
        //pre
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        when(friendRepository.findByUserIdAndFriendId(user_a.getUserId(),user_b.getUserId()))
                .thenReturn(null);
        when(friendRepository.findByUserIdAndFriendId(user_b.getUserId(),user_a.getUserId()))
                .thenReturn(new Friend(user_b,user_a.getUserId()));

        //logic
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("b"));

        try {
            userService.makeRoomWithFriends(request);
        }catch (CustomException e){
            logErrorInformation(e);

            //validate
            assertThat(e.getErrorCode()).isEqualTo(CANNOT_FIND_FRIEND);
        }
    }

    @Test
    @DisplayName("방/생성, 참여인원/생성, 채팅/생성")
    public void h() {
        //pre
        RequestAddChatRoomDTO request = new RequestAddChatRoomDTO();
        request.setUserId("a");
        request.setFriendIds(Arrays.asList("b"));

        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();
        Room room = new Room(createdAt,updatedAt);
        ReflectionTestUtils.setField(room,"roomId",1L);

        User user_a = new User("a","a","");
        User user_b = new User("b","b","");

        Participant participant1 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());
        Participant participant2 = new Participant(user_a,room,"roomName",createdAt.toLocalDate(),updatedAt.toLocalDate());

        //mocking
        mockUserRepositoryFindById(Arrays.asList(user_a,user_b));
        mockFriendRepositoryFindByUserIdAndFriendId(user_a, user_b);
        mockRoomRepositorySave(room);

        //logic
        userService.makeRoomWithFriends(request);
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

    private void mockFriendRepositoryFindByUserIdAndFriendId(User user_a, User user_b) {
        when(friendRepository.findByUserIdAndFriendId(any(),any()))
                .thenAnswer(input -> {
                    var arg1 = input.getArgument(0);
                    var arg2 = input.getArgument(1);
                    if (user_a.getUserId().equals(arg1) && user_b.getUserId().equals(arg2)){
                        return new Friend(user_a,user_b.getUserId());
                    }
                    if (user_b.getUserId().equals(arg1) && user_a.getUserId().equals(arg2)) {
                        return new Friend(user_b,user_a.getUserId());
                    }
                    return Optional.ofNullable(null);
                });
    }

    private void mockRoomRepositorySave(Room room) {
        when(roomRepository.save(any(Room.class))).thenReturn(room);
    }

    private static void logErrorInformation(CustomException e) {
        log.info("Exception = {}, Details = {}", e.getErrorCode(), e.getErrorCode().getDetail());
    }
}
