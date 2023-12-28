package chatting.chat.domain.participant.dto;

import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.dto.UserDto;
import chatting.chat.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ParticipantDto {
    private Long participantId;
    private UserDto userDto;
    private Long roomId;
    private String roomName;
    private LocalDate createdAt;
    private LocalDate UpdatedAt;

    @Builder
    public ParticipantDto(Long participantId, UserDto userDto, Long roomId, String roomName,
        LocalDate createdAt, LocalDate updatedAt) {
        this.participantId = participantId;
        this.userDto = userDto;
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdAt = createdAt;
        UpdatedAt = updatedAt;
    }
}
