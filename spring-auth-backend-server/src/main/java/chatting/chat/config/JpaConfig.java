package chatting.chat.config;

import chatting.chat.domain.user.repository.UserRepository;

import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class JpaConfig {
    private final EntityManager em;
    private final UserRepository userRepository;
    @Autowired
    public JpaConfig(EntityManager em, UserRepository userRepository) {
        this.em = em;
        this.userRepository = userRepository;
    }
    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }
}
