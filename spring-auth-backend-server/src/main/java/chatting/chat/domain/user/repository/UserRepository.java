package chatting.chat.domain.user.repository;


import chatting.chat.domain.data.User;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("select u from User u where u.userId = :userId")
    List<User> findByUserId(@Param("userId") String userId);

    List<User> findAll();

}
