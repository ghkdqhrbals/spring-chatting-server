package chatting.chat.domain.room.service;

import chatting.chat.domain.data.Room;
import chatting.chat.domain.room.repository.RoomRepository;

import java.util.Optional;

public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<Room> findByRoomId(Long roomId) {
        return Optional.ofNullable(roomRepository.findByRoomId(roomId));
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }
}
