package chatting.chat.domain.user.repository;


import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Nullable
    User save(User item);

    @Nullable
    @Query("select u from User u where u.userId = :userId")
    User findByUserId(@Param("userId") String userId);

    List<User> findAll();

}
