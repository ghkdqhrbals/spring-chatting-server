package chatting.chat.domain.room.dto;

import chatting.chat.domain.user.dto.UserDto;
import jakarta.persistence.Column;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.*;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long roomId;
    private List<UserDto> users;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    public RoomDto(Long roomId, List<UserDto> users, ZonedDateTime createdAt,
        ZonedDateTime updatedAt) {
        this.roomId = roomId;
        this.users = users;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
