package com.example.shopuserservice.web.security.token;

import org.springframework.data.repository.CrudRepository;

public interface UserRedisSessionRepository extends CrudRepository<UserRedisSession, String> {
}
