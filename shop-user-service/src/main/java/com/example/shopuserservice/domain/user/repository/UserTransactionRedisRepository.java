package com.example.shopuserservice.domain.user.repository;

import com.example.shopuserservice.domain.data.UserTransactions;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserTransactionRedisRepository extends CrudRepository<UserTransactions, UUID> {

}
