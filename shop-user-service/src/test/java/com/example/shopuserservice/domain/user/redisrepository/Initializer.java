package com.example.shopuserservice.domain.user.redisrepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

import java.io.IOException;

import static com.example.shopuserservice.web.util.server.PortChecker.isPortAvailable;

@Slf4j
public class Initializer {
    private static RedisServer redisServer;
    public static int redisPort;
    @BeforeAll
    public static void startRedis() throws IOException {
        log.trace("startRedis method start");
        for (int port = 10000; port <= 65535; port++) {
            log.trace("Check Port: " + port);
            if (isPortAvailable(port)) {
                log.trace("Found Available Port: " + port);
                redisPort = port;
                redisServer = new RedisServer(redisPort);
                redisServer.start();
                log.trace("Local Redis Server is Started");
                break;
            }else{
                log.trace("Check another port");
            }
        }
    }
    @AfterAll
    public static void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            log.trace("Local Redis Server Stop");
        }else{
            log.trace("Local Redis Server Already Stopped");
        }
    }
}
