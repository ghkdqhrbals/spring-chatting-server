package chatting.chat.domain.room.repository;

import chatting.chat.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Room save(Room room);

    @Nullable
    Room findByRoomId(@Param("roomId") Long roomId);

    List<Room> findAll();
}
