package com.example.shopuserservice.domain.user.redisrepository;

import com.example.shopuserservice.domain.user.data.UserTransactions;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserTransactionRedisRepository extends CrudRepository<UserTransactions, UUID> {

}
