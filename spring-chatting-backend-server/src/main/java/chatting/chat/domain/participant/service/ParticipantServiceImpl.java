package chatting.chat.domain.participant.service;

import chatting.chat.domain.participant.dto.ParticipantDto;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.repository.UserRepository;
import com.example.commondto.dto.participant.ParticipantRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commondto.error.ErrorCode.*;
import com.example.commondto.error.CustomException;

@Slf4j
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public ParticipantServiceImpl(ParticipantRepository participantRepository,
        UserRepository userRepository, RoomRepository roomRepository) {
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * 유저 아이디로 현재 참여중인 채팅방 조회
     * @param userId
     * @return List {@link ParticipantDto}
     */
    @Override
    public List<ParticipantDto> findAllByUserId(String userId) {
        return participantRepository.findAllByUserId(userId).stream().map(Participant::toDto).toList();
    }

    @Override
    public List<ParticipantDto> findParticipantByRoomId(Long roomId) {
        return participantRepository.findAllByRoomId(roomId).stream().map(Participant::toDto).toList();
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
    public String remove(Long roomId, String userId) throws CustomException {
        Participant participant = participantRepository.findByRoomIdAndUserId(roomId, userId);
        if (participant == null){
            throw new CustomException(INVALID_PARTICIPANT);
        }
        participantRepository.delete(participant);
        return "success";
    }

    @Override
    public String addParticipant(Long roomId, String userId) throws CustomException {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));

        Participant participant = Participant.builder()
            .room(room)
            .user(user)
            .build();

        participantRepository.save(participant);
        return "success";
    }
}
