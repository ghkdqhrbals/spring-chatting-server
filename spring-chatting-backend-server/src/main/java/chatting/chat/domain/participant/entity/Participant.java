package chatting.chat.domain.participant.entity;

import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.dto.UserDto;
import chatting.chat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ROOM_ID")
    private Room room;

    @Column(name = "ROOM_NAME")
    private String roomName;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDate UpdatedAt;

    @Builder
    public Participant(User user, Room room, String roomName, LocalDate createdAt, LocalDate updatedAt) {
        this.user = user;
        this.room = room;
        this.roomName = roomName;
        this.createdAt = createdAt;
        this.UpdatedAt = updatedAt;
    }

    public Participant() {

    }

    public ParticipantDto toDto(){
        return ParticipantDto.builder()
            .participantId(this.participantId)
            .userDto(this.user.toDto())
            .roomId(this.room.getRoomId())
            .roomName(this.roomName)
            .createdAt(this.createdAt)
            .updatedAt(this.UpdatedAt)
            .build();
    }
}
