package com.example.shopuserservice.domain.user.repository;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, Long> {

    @Query("select u from UserTransaction u where u.eventId = :eventId")
    Optional<UserTransaction> findByEventId(@Param("eventId") String eventId);

    List<UserTransaction> findAllByUserIdOrderByCreatedAt(String userID, Pageable pageable);
}
