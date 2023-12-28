package chatting.chat.domain.room.service;



import static org.assertj.core.api.Assertions.assertThat;
import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
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
class RoomServiceImplTest extends Initializer {
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




}