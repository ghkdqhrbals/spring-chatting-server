package chatting.chat.domain.user.repository;


import chatting.chat.domain.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    // The method "save" always returns you the same object you are going to save.

    @Nullable
    @Query("select u from User u where u.userId = :userId")
    User findByUserId(@Param("userId") String userId);

    List<User> findAll();

}
