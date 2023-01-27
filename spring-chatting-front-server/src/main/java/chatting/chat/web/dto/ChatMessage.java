package chatting.chat.web.dto;

import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ChatMessage {
    private Long roomId;
    private String writer;
    private String writerId;
    private String message;
    private ZonedDateTime createAt;

    public Chatting convertToChatting(Room room, User user){
        return new Chatting(roomId, room, user, message, createAt.toLocalDateTime());
    }
}