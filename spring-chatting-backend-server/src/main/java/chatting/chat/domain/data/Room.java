package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ROOM_ID")
    private Long roomId;
    @Column(name = "CREATED_AT")
    private ZonedDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private ZonedDateTime updatedAt;

    public Room(ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Room() {
    }
}
