package chatting.chat.domain.participant.service;

import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.participant.entity.Participant;

import chatting.chat.domain.user.entity.User;
import com.example.commondto.dto.participant.ParticipantRequest;
import com.example.commondto.error.CustomException;
import java.util.List;

public interface ParticipantService {
    List<ParticipantDto> findAllByUserId(String userId);
    List<ParticipantDto> findParticipantByRoomId(Long roomId);
    String save(Participant participant);
    String remove(Long roomId, String userId) throws CustomException;
    String addParticipant(Long roomId, String userId) throws CustomException;


}
