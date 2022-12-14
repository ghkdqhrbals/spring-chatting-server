package chatting.chat.web.dto;

import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private Long roomId;
    private String writer;
    private String message;
}