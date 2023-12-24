package chatting.chat.domain.chat.api;

import chatting.chat.domain.chat.service.ChatService;
import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.*;
import com.example.commondto.dto.chat.ChatRequest;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTOsWithUser;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RestController
@Transactional
@AllArgsConstructor
public class ChatController {

    private final UserService userService;
    private final RoomService roomService;
    private final ChatService chatService;



    @GetMapping("/chats")
    @Operation(summary = "Get chat records")
    public ResponseEntity<?> findChatRecords(@RequestParam("roomId") Long roomId) {
        Room findRoom = roomService.findByRoomId(roomId);
        List<Chatting> findChattings = chatService.findAllByRoomId(findRoom.getRoomId());
        ChatRequest.ChatRecordDTOsWithUser response = ChatRecordDTOsWithUser.builder()
            .records(
                findChattings.stream().map(
                    Chatting::toChatRecordDTO).collect(Collectors.toList()))
            .userId(UserContext.getUserId())
            .userName(userService.findById(UserContext.getUserId()).getUserName())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/chat")
    @Operation(summary = "Get a single chat with chat id")
    public ResponseEntity<?> findChatRecord(@RequestParam("chatId") Long chatId) {
        Chatting findChatting = chatService.findById(chatId);
        return ResponseEntity.ok(findChatting);
    }

    @PostMapping("/status")
    @Operation(summary = "Change user's status")
    public ResponseEntity<?> changeUserStatus(@RequestBody RequestChangeUserStatusDTO request) {
        userService.updateUserStatus(request);
        return ResponseEntity.ok("success");
    }

    // 채팅 저장
    @PostMapping(value = "/chat")
    @Operation(summary = "Save chat")
    public ResponseEntity<?> addChat(@RequestBody RequestAddChatMessageDTO req) {

        // validation
        Room findRoom = roomService.findByRoomId(req.getRoomId());
        User findUser = userService.findById(req.getWriterId());

        // service-logic
        Chatting chatting = createChatting(findRoom, findUser, req.getMessage());
        chatService.save(chatting);

        return ResponseEntity.ok("success");
    }


    /**
     * 채팅 생성
     * @param room
     * @param user
     * @param msg
     * @return {@link Chatting}
     */
    private Chatting createChatting(Room room, User user, String msg) {
        Chatting chatting = new Chatting();
        chatting.setId(UUID.randomUUID().toString());
        chatting.setRoom(room);
        chatting.setSendUser(user);
        chatting.setCreatedAt(LocalDateTime.now());
        chatting.setMessage(msg);
        return chatting;
    }

}
