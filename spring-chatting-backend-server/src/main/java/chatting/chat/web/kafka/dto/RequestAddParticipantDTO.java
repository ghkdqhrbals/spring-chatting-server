package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RequestAddParticipantDTO {
    private String userId;
    private String participantId;
}
