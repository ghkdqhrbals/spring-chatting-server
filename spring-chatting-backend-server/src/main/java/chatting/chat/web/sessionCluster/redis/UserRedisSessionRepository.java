package chatting.chat.web.sessionCluster.redis;

import org.springframework.data.repository.CrudRepository;

public interface UserRedisSessionRepository extends CrudRepository<UserRedisSession, String> {
}
