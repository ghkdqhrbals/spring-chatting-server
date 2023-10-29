package chatting.chat.web;

import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.entity.User;
import com.example.commondto.dto.friend.FriendResponse;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestUser;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RestController
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
        List<ChatRecord> response = findChattings.stream().map(
                c -> new ChatRecord(c.getId(), c.getRoom().getRoomId(), c.getSendUser().getUserId(),
                    c.getSendUser().getUserName(), c.getMessage(), c.getCreatedAt()))
            .collect(Collectors.toList());

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


    // utils
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
