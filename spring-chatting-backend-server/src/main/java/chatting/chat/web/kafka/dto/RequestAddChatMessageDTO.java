package chatting.chat.web.kafka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RequestAddChatMessageDTO {
    private Long roomId;
    private String message;

    @Builder
    public RequestAddChatMessageDTO(Long roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }
}