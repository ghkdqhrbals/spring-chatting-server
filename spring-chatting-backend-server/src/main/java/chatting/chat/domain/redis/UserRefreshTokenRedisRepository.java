package chatting.chat.domain.redis;

import org.springframework.data.repository.CrudRepository;


public interface UserRefreshTokenRedisRepository extends CrudRepository<UserRefreshToken, String>{

}
