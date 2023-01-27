package chatting.chat.web.kafka.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ChatRecord {
    private String id;
    private Long roomId;
    private String sendUserId;
    private String sendUserName;
    private String message;
    private LocalDateTime createdAt;

    public ChatRecord(String id, Long roomId, String sendUserId, String sendUserName, String message, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.sendUserId = sendUserId;
        this.sendUserName = sendUserName;
        this.message = message;
        this.createdAt = createdAt;
    }
}
