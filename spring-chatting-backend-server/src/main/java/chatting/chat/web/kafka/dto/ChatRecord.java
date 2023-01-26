package chatting.chat.web.kafka.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ChatRecord {
    private Long id;
    private Long roomId;
    private String sendUserId;
    private String sendUserName;
    private String message;
    private LocalDate createdDate;
    private LocalTime createdTime;

    public ChatRecord(Long id, Long roomId, String sendUserId, String sendUserName, String message, LocalDate createdDate, LocalTime createdTime) {
        this.id = id;
        this.roomId = roomId;
        this.sendUserId = sendUserId;
        this.sendUserName = sendUserName;
        this.message = message;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }
}
