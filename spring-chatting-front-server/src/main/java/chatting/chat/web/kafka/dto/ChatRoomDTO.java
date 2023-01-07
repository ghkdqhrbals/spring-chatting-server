package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
