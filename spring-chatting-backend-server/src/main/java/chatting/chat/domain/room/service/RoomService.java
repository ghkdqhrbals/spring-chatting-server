package chatting.chat.domain.room.service;

import chatting.chat.domain.room.entity.Room;

public interface RoomService {
    Room findByRoomId(Long roomId);
    Room save(Room room);

    void remove(Long roomId);
}
