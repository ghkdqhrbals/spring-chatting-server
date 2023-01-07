package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ROOM_ID")
    private Long roomId;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDate updatedAt;

    public Room(LocalDate createdAt, LocalDate updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Room() {
    }
}
