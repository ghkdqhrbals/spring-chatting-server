package chatting.chat.domain.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRefreshTokenRedisRepository extends CrudRepository<UserRefreshToken, String>{

}
