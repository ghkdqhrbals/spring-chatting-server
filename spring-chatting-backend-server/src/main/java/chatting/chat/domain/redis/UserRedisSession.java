package chatting.chat.domain.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@ToString
@RedisHash(value = "UserRedisSession", timeToLive = 6000)
public class UserRedisSession implements Serializable{

    @Id
    private String refreshToken;
    private String userId;

    @Builder
    public UserRedisSession(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
