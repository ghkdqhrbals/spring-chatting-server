package chatting.chat.domain.participant.api;

import static com.example.commondto.error.ErrorCode.CANNOT_FIND_ROOM;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.participant.service.ParticipantServiceImpl;
import chatting.chat.domain.user.api.UserController;
import chatting.chat.domain.user.dto.UserDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.error.GlobalExceptionHandler;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.filter.UserContextInterceptor;
import chatting.chat.web.sessionCluster.redis.UserRedisSession;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import chatting.chat.web.token.JwtTokenValidator;
import com.example.commondto.error.CustomException;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({SpringExtension.class})
@WebMvcTest(ParticipantController.class)
@ActiveProfiles("testAPI")
@Import({GlobalExceptionHandler.class, JwtTokenValidator.class})
@Slf4j
class ParticipantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserContext userContext;

    @MockBean
    private ParticipantServiceImpl participantService;

    @MockBean
    private UserRedisSessionRepository userRedisSessionRepository;

    @Autowired
    private UserContextInterceptor userContextInterceptor;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new ParticipantController(userContext,participantService))
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
    void whenRoomNotValid_then404ShouldBeReturned() throws Exception {
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
    @DisplayName("내가 참여중인 채팅방 목록 조회 시 참여중인 채팅방 목록 반환 성공")
    void whenValidUserGetHisParticipantList_thenValidParticipantListShouldBeReturned()
        throws Exception {
        // given
        String refreshToken = "validToken";
        String userId = "userId";
        ParticipantDto participantDto = ParticipantDto.builder().userDto(UserDto.builder().userId(userId).userName("userName").userStatus("").build())
            .participantId(1L)
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .roomId(1L)
            .roomName("roomName")
            .build();
        UserRedisSession userRedisSession = new UserRedisSession(userId, refreshToken);
        when(userRedisSessionRepository.findById(refreshToken)).thenReturn(Optional.of(userRedisSession));
        when(participantService.findAllByUserId(userId)).thenReturn(Arrays.asList(participantDto));

        // when + then
        mockMvc.perform(get("/participant").cookie(new Cookie("refreshToken", refreshToken)))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].userDto.userId", is(userId)));
    }
}