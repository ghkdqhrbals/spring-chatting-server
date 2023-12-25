package chatting.chat.domain.chat.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.chat.service.ChatService;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.api.UserController;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.error.GlobalExceptionHandler;
import chatting.chat.web.filter.UserContextInterceptor;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private ChatService chatService;
    @MockBean
    private RoomServiceImpl roomService;

    @MockBean
    private UserRedisSessionRepository userRedisSessionRepository;

    @Autowired
    private UserContextInterceptor userContextInterceptor;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new ChatController(userService, roomService, chatService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(userContextInterceptor)
            .build();
    }
}