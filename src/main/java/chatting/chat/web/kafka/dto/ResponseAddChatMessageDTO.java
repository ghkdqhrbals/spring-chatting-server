package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ResponseAddChatMessageDTO {
    private Long roomId;
    private String userId;
    private String message;
    private Boolean isSuccess;
    private ZonedDateTime createdAt;
    private String errorMessage;

    public ResponseAddChatMessageDTO(Long roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
        this.isSuccess=false;
        this.errorMessage="";
    }

    public ResponseAddChatMessageDTO() {
        this.isSuccess=false;
        this.errorMessage="";
    }
}
