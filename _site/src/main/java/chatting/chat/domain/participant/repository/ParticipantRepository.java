package chatting.chat.domain.participant.repository;

import chatting.chat.domain.data.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Nullable
    @Query("select p from Participant p where p.user.userId = :userId")
    List<Participant> findAllByUserId(@Param("userId") String userId);

    @Nullable
    @Query("select p from Participant p where p.room.roomId = :roomId")
    List<Participant> findAllByRoomId(@Param("roomId") Long roomId);

    @Nullable
    @Query("select p from Participant p where p.room.roomId = :roomId and p.user.userId = :userId")
    Participant findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") String userId);
}
