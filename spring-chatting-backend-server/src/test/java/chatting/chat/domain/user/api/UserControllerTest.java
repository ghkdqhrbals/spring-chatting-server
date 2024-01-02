package chatting.chat.domain.user.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chatting.chat.web.error.GlobalExceptionHandler;
import chatting.chat.web.filter.UserContext;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestUser;
import chatting.chat.web.filter.UserContextInterceptor;
import chatting.chat.web.sessionCluster.redis.UserRedisSession;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({SpringExtension.class})
@WebMvcTest(UserController.class)
@ActiveProfiles("testAPI")
@Import(GlobalExceptionHandler.class)
@Slf4j
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @SpyBean
    private UserContext userContext;

    @MockBean
    private UserRedisSessionRepository userRedisSessionRepository;

    @Autowired
    private UserContextInterceptor userContextInterceptor;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new UserController(userContext, userService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(userContextInterceptor)
            .build();
    }

    @Test
    @DisplayName("유효한 refreshToken으로 사용자 정보 조회")
    void whenFindUserWithValidToken_thenReturnUserInfo() throws Exception {
        // given
        String refreshToken = "validToken";
        String userId = "userId";
        User testUser = new User(userId, "Test User", "");
        when(userService.findById(userId)).thenReturn(testUser);
        when(userRedisSessionRepository.findById(refreshToken)).thenReturn(
            Optional.of(new UserRedisSession(userId, refreshToken)));

        // when + then
        mockMvc.perform(get("/user").cookie(new Cookie("refreshToken", refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId", is(userId)));
    }

    @Test
    @DisplayName("유효하지 않은 refreshToken으로 사용자 정보 조회 시 실패")
    void whenFindUserWithInvalidToken_thenUnauthorized() throws Exception {
        // given
        String invalidToken = "invalidToken";

        // when + then
        mockMvc.perform(get("/user").cookie(new Cookie("refreshToken", invalidToken)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효한 사용자 데이터로 새 사용자 추가")
    void whenAddUserWithValidData_thenSaveUser() throws Exception {
        // given
        RequestUser req = new RequestUser("validUserId", "validUserPw", "user@example.com",
            "Valid User", "UserRole");
        User savedUser = new User(req.getUserId(), req.getUserName(), "");
        when(userService.save(anyString(), anyString(), anyString())).thenReturn(savedUser);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(result -> log.info(result.getResponse().getContentAsString()))
            .andExpect(jsonPath("$.userId", is(req.getUserId())))
            .andExpect(jsonPath("$.userName", is(req.getUserName())));
    }

    @Test
    @DisplayName("유효하지 않은 사용자 데이터로 새 사용자 추가 시 실패")
    void whenAddUserWithInvalidData_thenFail() throws Exception {
        // given
        RequestUser invalidReq = new RequestUser("u", "pwd", "invalidEmail", "Us", "Rl");

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidReq)))
            .andExpect(status().isBadRequest())
            .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }
}
