package chatting.chat.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatRoomDTO {
    private Long roomId;
    private String roomName;
    private List<String> participantNames;

    public ChatRoomDTO(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
