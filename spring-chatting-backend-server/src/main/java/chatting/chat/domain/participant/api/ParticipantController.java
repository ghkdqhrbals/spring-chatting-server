package chatting.chat.domain.participant.api;

import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.web.filter.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class ParticipantController {

    private final UserContext userContext;
    private final ParticipantService participantService;

    /**
     * 내가 참여중인 채팅방에서 나가기
     * @param roomId
     * @implNote {@link UserContext#getUserId()} 가 필요합니다.
     * {@link UserContext#setUserId(String)} 를 통해 userId 를 저장해주세요.
     * UserConext 의 userId 는 {@link chatting.chat.web.filter.UserContextInterceptor} 에서 자동으로 설정됩니다.
     * @return if success return "success" else throw {@link com.example.commondto.error.CustomException}
     */
    @DeleteMapping("/participant")
    @Operation(summary = "Delete a participant")
    public String removeParticipant(@RequestParam Long roomId) {
        return participantService.remove(roomId, userContext.getUserId());
    }

    /**
     * 채팅방에 참여
     * @param roomId
     * @implNote {@link UserContext#getUserId()} 가 필요합니다.
     * {@link UserContext#setUserId(String)} 를 통해 userId 를 저장해주세요.
     * UserConext 의 userId 는 {@link chatting.chat.web.filter.UserContextInterceptor} 에서 자동으로 설정됩니다.
     * @return if success return "success" else throw {@link com.example.commondto.error.CustomException}
     */
    @PostMapping("/participant")
    @Operation(summary = "Add a participant")
    public String addParticipant(@RequestParam Long roomId) {
        return participantService.addParticipant(roomId, userContext.getUserId());
    }

    /**
     * 채팅방에 참여중인 유저 목록 조회
     * @return List {@link ParticipantDto}
     */
    @GetMapping("/participants")
    @Operation(summary = "Get List of participants by room id")
    public List<ParticipantDto> getParticipants(@RequestParam Long roomId) {
        return participantService.findParticipantByRoomId(roomId);
    }

    /**
     * 내가 참여중인 채팅방 목록 조회
     * @return List {@link ParticipantDto}
     */
    @GetMapping("/participant")
    @Operation(summary = "Get List of my participants")
    public List<ParticipantDto> getParticipant() {
        return participantService.findAllByUserId(userContext.getUserId());
    }
}
