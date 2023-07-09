package chatting.chat.domain.friend.repository;

import chatting.chat.domain.compositekey.FriendId;
import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Nullable
    Friend save(Friend friend);

    @Query("select f from Friend f where f.user.userId = :userId")
    List<Friend> findAllByUserId(@Param("userId") String userId);

    @Nullable
    @Query("select f from Friend f where f.user.userId = :userId and f.friendId = :friendId")
    Friend findByUserIdAndFriendId(@Param("userId") String userId, @Param("friendId") String friendId);

}
