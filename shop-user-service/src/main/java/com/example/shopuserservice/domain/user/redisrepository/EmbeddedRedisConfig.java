package com.example.shopuserservice.domain.user.redisrepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;


@Slf4j //lombok
@Profile("local") // profile이 local일때만 활성화
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    public EmbeddedRedisConfig() {
        log.info("Local Redis Server");
    }

    @PostConstruct
    public void redisServer() throws IOException {
        log.info("Local Redis Server Start");
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        log.info("Local Redis Server Stop");
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
