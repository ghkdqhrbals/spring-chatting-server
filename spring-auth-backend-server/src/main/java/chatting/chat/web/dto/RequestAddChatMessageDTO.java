package chatting.chat.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddChatMessageDTO {
    private Long roomId;
    private String writer;
    private String writerId;
    private String message;
}