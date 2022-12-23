package chatting.chat.web.kafka.dto;

import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class RequestAddChatMessageDTO {
    private Long roomId;
    private String writer;
    private String writerId;
    private String message;
}