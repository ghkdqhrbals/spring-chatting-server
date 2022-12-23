package chatting.chat.domain.room.service;

import chatting.chat.domain.data.Room;

import java.util.Optional;

public interface RoomService {
    Optional<Room> findByRoomId(Long roomId);
    Room save(Room room);
}
