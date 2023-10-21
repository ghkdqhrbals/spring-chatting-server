package com.example.shopuserservice.web.security.token;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@ToString
@RedisHash(value = "userRedisSession", timeToLive = 6000)
public class UserRedisSession implements Serializable{

    private String userId;

    @Id
    private String refreshToken;

    @Builder
    public UserRedisSession(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
