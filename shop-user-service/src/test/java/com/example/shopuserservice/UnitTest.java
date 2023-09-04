package com.example.shopuserservice;

import com.example.shopuserservice.config.HikariConfig;
import com.example.shopuserservice.domain.user.redisrepository.UserTransactionRedisRepository;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC;
import com.example.shopuserservice.domain.user.service.UserCommandQueryServiceImpl;
import com.example.shopuserservice.domain.user.service.UserReadService;
import com.example.shopuserservice.domain.user.service.UserReadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
public class UnitTest {
    @SpyBean
    protected UserRepositoryJDBC userRepositoryJDBC;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected UserTransactionRedisRepository userTransactionRedisRepository;

    @MockBean
    protected KafkaTemplate<String, Object> kafkaProducerTemplate;


    @BeforeEach
    void setup(){
        userRepository.deleteAll();
        userTransactionRedisRepository.deleteAll();
    }
}
