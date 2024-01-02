package chatting.chat.domain.room.api;

import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.domain.room.dto.RoomDto;
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

    private final UserContext userContext;
    private final RoomService roomService;
    private final UserService userService;
    private final ParticipantService participantService;

    @GetMapping(value = "/room/{roomId}")
    @Operation(summary = "Get room information")
    public ResponseEntity<?> findRoom(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(roomService.findByRoomId(roomId));
    }

    @GetMapping(value = "/rooms")
    @Operation(summary = "Get room information that user participated")
    public ResponseEntity<?> findRoomWithUserId() {
        return ResponseEntity.ok(userService.findAllMyRooms(userContext.getUserId()));
    }

    // 채팅방 개설
    @PostMapping("/room")
    @Operation(summary = "Open room with friends")
    public ResponseEntity<?> addChatRoom(@RequestBody RequestAddChatRoomDTO req){
        userService.makeRoomWithFriends(req);
        List<ChatRoomDTO> allMyRooms = userService.findAllMyRooms(userContext.getUserId());
        return ResponseEntity.ok(allMyRooms);
    }
}
