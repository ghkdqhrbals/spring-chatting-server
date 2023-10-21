package com.example.shopuserservice.domain.user.redisrepository;

import com.example.shopuserservice.domain.user.data.UserRefreshToken;
import org.springframework.data.repository.CrudRepository;


public interface UserRefreshTokenRedisRepository extends CrudRepository<UserRefreshToken, String>{

}
