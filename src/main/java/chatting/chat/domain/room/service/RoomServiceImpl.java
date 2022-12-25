package chatting.chat.domain.room.service;

import chatting.chat.domain.data.Room;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.web.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.CANNOT_FIND_ROOM;

@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room findByRoomId(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (!findRoom.isPresent()){
            throw new CustomException(CANNOT_FIND_ROOM);
        }
        return findRoom.get();
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }
}
