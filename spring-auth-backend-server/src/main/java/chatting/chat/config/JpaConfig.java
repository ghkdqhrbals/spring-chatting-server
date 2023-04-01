package chatting.chat.config;

import chatting.chat.domain.user.repository.UserRepository;

import chatting.chat.domain.user.repository.UserRepositoryJDBC;
import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.domain.user.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class JpaConfig {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final JdbcTemplate jdbcTemplate;
    private final Executor serviceExecutor;

    private final HikariDataSource hikariDataSource;

    public JpaConfig(EntityManager em,
                     UserRepository userRepository,
                     KafkaTemplate<String, Object> kafkaProducerTemplate,
                     UserRepositoryJDBC userRepositoryJDBC,
                     JdbcTemplate jdbcTemplate,
                     @Qualifier("taskExecutorForService") Executor serviceExecutor, HikariDataSource hikariDataSource) {
        this.em = em;
        this.userRepository = userRepository;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
        this.userRepositoryJDBC = userRepositoryJDBC;
        this.jdbcTemplate = jdbcTemplate;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
    }

//    @Bean
//    public UserService userService() {
//        return new UserServiceImpl(userRepository, userRepositoryJDBC, kafkaProducerTemplate, serviceExecutor, hikariDataSource);
//    }
}
