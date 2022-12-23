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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class JpaConfig {

    private final EntityManager em;
    private final FriendRepository friendRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    @Autowired
    public JpaConfig(EntityManager em, UserRepository userRepository,FriendRepository friendRepository, RoomRepository roomRepository, ParticipantRepository participantRepository) {
        this.em = em;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository, roomRepository, participantRepository, friendRepository);
    }

    @Bean
    public FriendService friendService(){
        return new FriendServiceImpl(friendRepository);
    }

}
