package chatting.chat.domain.redis;


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
public class UserRefreshToken implements Serializable {

    @Id
    private String refreshToken;
    private String userId;

    @Builder
    public UserRefreshToken(String refreshToken, String userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
