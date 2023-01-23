package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
public class Participant {
    private Long participantId;
    private User user;
    private Room room;
    private String roomName;
    private LocalDate createdAt;
    private LocalDate UpdatedAt;

    public Participant(User user, Room room, String roomName, LocalDate createdAt, LocalDate updatedAt) {
        this.user = user;
        this.room = room;
        this.roomName = roomName;
        this.createdAt = createdAt;
        this.UpdatedAt = updatedAt;
    }

    public Participant() {

    }
}
