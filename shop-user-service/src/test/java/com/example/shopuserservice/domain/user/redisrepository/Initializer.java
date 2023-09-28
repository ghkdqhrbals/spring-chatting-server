package com.example.shopuserservice.domain.user.redisrepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

import java.io.IOException;

public class Initializer {
    private static RedisServer redisServer;
    private static final int redisPort=6379;

    @BeforeAll
    public static void startRedis() throws IOException {
        if (redisServer == null || !redisServer.isActive()) {
            redisServer = new RedisServer(redisPort);
            redisServer.start();
            System.out.println("Local Redis Server Start");
        }else{
            System.out.println("Local Redis Server Already Running");
        }
    }

    @AfterAll
    public static void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            System.out.println("Local Redis Server Stop");
        }else{
            System.out.println("Local Redis Server Already Stopped");
        }
    }
}
