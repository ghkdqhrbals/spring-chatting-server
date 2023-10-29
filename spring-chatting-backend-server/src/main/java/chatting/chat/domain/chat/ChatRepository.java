package chatting.chat.domain.chat;

import chatting.chat.domain.chat.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chatting, Long> {

    @Nullable
    @Query("select c from Chatting c where c.room.roomId = :roomId")
    List<Chatting> findAllByRoomId(@Param("roomId") Long roomId); // in asc order by its date and time



}
