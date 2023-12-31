package chatting.chat.domain.participant.api;

import static com.example.commondto.error.ErrorCode.CANNOT_FIND_ROOM;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.user.api.UserController;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.error.GlobalExceptionHandler;
import chatting.chat.web.filter.UserContextInterceptor;
import chatting.chat.web.sessionCluster.redis.UserRedisSession;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import com.example.commondto.error.CustomException;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({SpringExtension.class})
@WebMvcTest(ParticipantController.class)
@ActiveProfiles("testAPI")
@Import(GlobalExceptionHandler.class)
@Slf4j
class ParticipantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantServiceImpl participantService;

    @MockBean
    private UserRedisSessionRepository userRedisSessionRepository;

    @Autowired
    private UserContextInterceptor userContextInterceptor;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new ParticipantController(participantService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(userContextInterceptor)
            .build();
    }


    @Test
    @DisplayName("채팅방에 참여 시 참여 성공")
    void whenValidParticipantAdd_thenSuccessShouldBeReturned() throws Exception {
        // given
        String refreshToken = "validToken";
        String userId = "userId";
        UserRedisSession userRedisSession = new UserRedisSession(userId, refreshToken);
        when(userRedisSessionRepository.findById(refreshToken)).thenReturn(Optional.of(userRedisSession));
        when(participantService.addParticipant(1L,userId)).thenReturn("success");

        // when + then
        mockMvc.perform(post("/participant").param("roomId","1").cookie(new Cookie("refreshToken", refreshToken)))
            .andExpect(status().isOk()).andExpect(jsonPath("$", is("success")));
    }

    @Test
    @DisplayName("채팅방이 존재하지 않을 때 참여 시 에러 반환")
    void addParticipant() throws Exception {
        // given
        String refreshToken = "validToken";
        String userId = "userId";
        UserRedisSession userRedisSession = new UserRedisSession(userId, refreshToken);
        when(userRedisSessionRepository.findById(refreshToken)).thenReturn(Optional.of(userRedisSession));
        when(participantService.addParticipant(1L,userId)).thenThrow(new CustomException(CANNOT_FIND_ROOM));

        // when + then
        mockMvc.perform(post("/participant").param("roomId","1").cookie(new Cookie("refreshToken", refreshToken)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getParticipants() {
    }

    @Test
    void getParticipant() {
    }
}