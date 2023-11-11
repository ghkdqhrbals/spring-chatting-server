package chatting.chat.domain.room.api;

import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.filter.UserContext;
import chatting.chat.web.kafka.dto.ChatRoomDTO;
import chatting.chat.web.kafka.dto.RequestAddChatRoomDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;

    @GetMapping(value = "/room/{roomId}")
    @Operation(summary = "Get room information")
    public ResponseEntity<?> findRoom(@PathVariable("roomId") Long roomId) {
        Room room = roomService.findByRoomId(roomId);
        return ResponseEntity.ok(room);
    }

    @GetMapping(value = "/rooms")
    @Operation(summary = "Get room information that user participated")
    public ResponseEntity<?> findRoomWithUserId() {
        List<ChatRoomDTO> findUserRooms = userService.findAllMyRooms(UserContext.getUserId());
        return ResponseEntity.ok(findUserRooms);
    }

    // 채팅방 개설
    @PostMapping("/room")
    @Operation(summary = "Open room with friends")
    public ResponseEntity<?> addChatRoom(@RequestBody RequestAddChatRoomDTO req){
        userService.makeRoomWithFriends(req);
        List<ChatRoomDTO> allMyRooms = userService.findAllMyRooms(UserContext.getUserId());
        return ResponseEntity.ok(allMyRooms);
    }
}
