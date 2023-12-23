package chatting.chat.initial;

import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.friend.service.FriendServiceImpl;
import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(RedisInitializer.class)
public class Initializer {
    @Autowired
    protected UserServiceImpl userService;
    @Autowired
    protected RoomServiceImpl roomServiceImpl;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected FriendRepository friendRepository;
    @Autowired
    protected FriendServiceImpl friendService;
    @Autowired
    protected ParticipantServiceImpl participantService;
}