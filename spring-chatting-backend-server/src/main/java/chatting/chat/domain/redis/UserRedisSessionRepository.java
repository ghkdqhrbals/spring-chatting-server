package chatting.chat.domain.redis;

import org.springframework.data.repository.CrudRepository;

public interface UserRedisSessionRepository extends CrudRepository<UserRedisSession, String> {
}
