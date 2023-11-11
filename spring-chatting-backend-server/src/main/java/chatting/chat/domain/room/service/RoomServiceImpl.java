package chatting.chat.domain.room.service;

import static com.example.commondto.error.ErrorCode.*;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import com.example.commondto.error.AppException;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.room.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Override
    public void remove(Long roomId){
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (!findRoom.isPresent()){
            throw new CustomException(CANNOT_FIND_ROOM);
        }

        roomRepository.deleteById(roomId);
    }
}
