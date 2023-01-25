package chatting.chat.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDTO {
    private Long roomId;
    private String roomName;

    public ChatRoomDTO(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
