package chatting.chat.domain.room.service;

import static com.example.commondto.error.ErrorCode.*;

import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import com.example.commondto.error.CustomException;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.room.repository.RoomRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
        ParticipantRepository participantRepository) {
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public Room findByRoomId(Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void remove(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));
        roomRepository.delete(room);
    }
}
