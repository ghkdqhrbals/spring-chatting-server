package com.example.shopuserservice.domain.user.repository;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, Long> {

    /**
     * 이벤트 트랜젝션을 업데이트하기 위한 쿼리입니다.
     * @implNote  비관적 쓰기 잠금을 통해 트랜젝션 정합성 보장
     * 이유는 5개 이상의 여러 파티션을 사용하고, 이를 concurrency 하게 읽기 때문입니다
     *
     * 만약 각각의 스레드가 별도의 트랜젝션으로 해당 row 를 잠금처리하지 않고 접근하게 된다면,
     * 동시처리로 인해 정합성이 보장되지 않기 때문이죠!
     *
     * 비관적 쓰기 잠금을 피하고 싶다면, 동일한 Thread로 수행하도록 작성해야합니다.
     * 이는 쉽게 작성할 수 있는데요.
     * userId를 key로 Kafka 파티션에 넣으면 가능합니다.
     * 이렇게 되면 하나의 스레드가 userId에 매핑되서 처리할 수 있게 되기 때문입니다.
     * 즉, Transaction 이 동시에 서로의 영역을 침범할 수 없게 되는것이죠!
     *
     * @param eventId
     * @return Optional UserTransaction
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserTransaction u where u.eventId = :eventId")
    Optional<UserTransaction> findByEventId(@Param("eventId") UUID eventId);

    List<UserTransaction> findAllByUserIdOrderByCreatedAt(String userID, Pageable pageable);
}
