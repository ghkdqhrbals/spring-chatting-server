package chatting.chat.web.kafka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddChatMessageDTO {
    private Long roomId;
    private String message;

    public RequestAddChatMessageDTO() {
    }

    @Builder
    public RequestAddChatMessageDTO(Long roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }
}