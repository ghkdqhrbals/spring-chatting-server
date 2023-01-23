package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
public class Room {
    private Long roomId;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Room(LocalDate createdAt, LocalDate updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Room() {
    }
}
