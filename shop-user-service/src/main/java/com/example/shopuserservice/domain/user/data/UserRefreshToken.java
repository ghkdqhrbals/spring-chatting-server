package com.example.shopuserservice.domain.user.data;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@ToString
@RedisHash(value = "UserRefreshToken", timeToLive = 600)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserRefreshToken implements Serializable {
    @Id
    private String userId;
    private String refreshToken;

    @Builder
    public UserRefreshToken(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
