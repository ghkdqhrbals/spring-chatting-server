package chatting.chat.web.dto;

import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ChattingDTO{
    private Long roomId;

    private String userId;

    private String message;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public ChattingDTO(Long roomId, String userId, String message, LocalDate createdAt, LocalDate updatedAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
