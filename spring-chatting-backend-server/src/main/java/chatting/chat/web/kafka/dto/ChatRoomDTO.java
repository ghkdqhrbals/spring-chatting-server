package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.*;
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDTO {
    private Long roomId;
    private String roomName;
    private List<String> participantNames;
}
