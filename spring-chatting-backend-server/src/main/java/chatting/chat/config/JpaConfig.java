package chatting.chat.config;

import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.friend.service.FriendServiceImpl;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.repository.UserRepository;

import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.filter.UserContext;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
@Profile("!testAPI")
public class JpaConfig {
    private final EntityManager em;
    private final FriendRepository friendRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaConfig(EntityManager em, UserRepository userRepository,FriendRepository friendRepository, RoomRepository roomRepository, ParticipantRepository participantRepository,
        UserContext userContext, JdbcTemplate jdbcTemplate) {
        this.em = em;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
        this.userContext = userContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userContext, userRepository, roomRepository, participantRepository, friendRepository);
    }

    @Bean
    public FriendService friendService(){
        return new FriendServiceImpl(friendRepository, userRepository);
    }

    @Bean
    public RoomService roomService() {
        return new RoomServiceImpl(roomRepository, participantRepository);
    }

    @Bean
    public ParticipantService participantService() {
        return new ParticipantServiceImpl(participantRepository, userRepository, roomRepository);
    }

}
