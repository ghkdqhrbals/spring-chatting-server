package chatting.chat.domain.participant.service;

import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commondto.error.ErrorCode.*;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import com.example.commondto.error.AppException;

@Slf4j
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;

    public ParticipantServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public List<Participant> findAllByUserId(String userId) {
        return participantRepository.findAllByUserId(userId);
    }

    @Override
    public String save(Participant participant) throws CustomException {
        Participant findParticipant = participantRepository.findByRoomIdAndUserId(participant.getRoom().getRoomId(), participant.getUser().getUserId());

        // 기존에 참여하던 방일 때
        if (findParticipant!=null){
            throw new CustomException(DUPLICATE_PARTICIPANT);
        }

        participantRepository.save(participant);
        return null;
    }

    @Override
    public String remove(Participant participant) {
        participantRepository.delete(participant);
        return null;
    }
}
