package chatting.chat.initial;

import chatting.chat.domain.chat.service.ChatService;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.friend.service.FriendServiceImpl;
import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(RedisInitializer.class)
public class Initializer {
    @Autowired
    protected UserServiceImpl userService;
    @Autowired
    protected RoomServiceImpl roomService;
    @Autowired
    protected ChatService chatService;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected FriendRepository friendRepository;
    @Autowired
    protected FriendServiceImpl friendService;
    @Autowired
    protected ParticipantServiceImpl participantService;
    @SpyBean
    protected RoomRepository roomRepository;
}