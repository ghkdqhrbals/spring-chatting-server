package chatting.chat.domain.chat.api;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.chat.service.ChatService;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.api.UserController;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.error.GlobalExceptionHandler;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.filter.UserContextInterceptor;
import chatting.chat.web.kafka.dto.RequestAddChatMessageDTO;
import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import chatting.chat.web.token.JwtTokenValidator;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatController.class)
@ActiveProfiles("testAPI")
@Import({GlobalExceptionHandler.class, JwtTokenValidator.class})
@Slf4j
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
    private UserContext userContext;

    @MockBean
    private UserRedisSessionRepository userRedisSessionRepository;

    @SpyBean
    private UserContextInterceptor userContextInterceptor;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new ChatController(userContext, userService, roomService, chatService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(userContextInterceptor)
            .build();
    }

    @Test
    @DisplayName("채팅 기록 조회 시 성공해야합니다.")
    public void whenFindChatRecords_thenReturnChatRecords() throws Exception {
        // given
        Long roomId = 1L;
        String userId = "testUserId";
        List<ChatRecordDTO> mockRecords = Arrays.asList(
            ChatRecordDTO.builder().id("chatId").message("test").createdAt(LocalDateTime.now())
                .sendUserId(userId).sendUserName("testName").roomId(1L).build());
        given(chatService.findAllByRoomId(eq(roomId))).willReturn(mockRecords);
        given(userContextInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(userContext.getUserId()).willReturn("testUserId");
        given(userService.findById(any())).willReturn(User.builder().userId(userId).userName("userName").build());

        // when + then
        mockMvc.perform(get("/chats?roomId=" + roomId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.records", hasSize(mockRecords.size())));
    }

    @Test
    @DisplayName("단일 채팅 조회 시 성공해야합니다.")
    public void whenFindChatRecord_thenReturnSingleChat() throws Exception {
        // given
        String chatId = "chatId";
        List<ChatRecordDTO> mockRecords = Arrays.asList(
            ChatRecordDTO.builder().id(chatId).message("test").createdAt(LocalDateTime.now())
                .sendUserId("testUserId").sendUserName("testName").roomId(1L).build());
        given(chatService.findById(eq(chatId))).willReturn(mockRecords.get(0));
        given(userContextInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(userContext.getUserId()).willReturn("testUserId");

        // when + then
        mockMvc.perform(get("/chat?chatId=" + chatId))
            .andDo((result)->log.trace(result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(chatId)));
    }

    @Test
    @DisplayName("채팅 저장 시 성공해야합니다.")
    public void whenAddChat_thenSaveChat() throws Exception {
        // given
        String userId = "testUserId";
        RequestAddChatMessageDTO req = RequestAddChatMessageDTO.builder()
            .roomId(1L)
            .message("test message").build();
        ChatRecordDTO chatToSave = ChatRecordDTO.builder()
            .id("randomChatId").message(req.getMessage()).createdAt(LocalDateTime.now())
            .sendUserId(userId).sendUserName("testName").roomId(req.getRoomId()).build();

        given(userContext.getUserId()).willReturn(userId);
        given(userContextInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(chatService.save(any(), eq(userId))).willReturn(chatToSave);



        // when + then
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andDo((result)->log.trace(result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is(chatToSave.getMessage())))
            .andExpect(jsonPath("$.sendUserId", is(chatToSave.getSendUserId())))
            .andExpect(jsonPath("$.sendUserName", is(chatToSave.getSendUserName())));
    }


}