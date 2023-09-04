package com.example.shopuserservice.config;


import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class JpaConfig {
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Executor serviceExecutor;
    private final HikariDataSource hikariDataSource;

    public JpaConfig(EntityManager entityManager,
                     UserRepository userRepository,
                     JdbcTemplate jdbcTemplate,
                     @Qualifier("taskExecutor") Executor serviceExecutor, HikariDataSource hikariDataSource) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.serviceExecutor = serviceExecutor;
        this.hikariDataSource = hikariDataSource;
    }

}
