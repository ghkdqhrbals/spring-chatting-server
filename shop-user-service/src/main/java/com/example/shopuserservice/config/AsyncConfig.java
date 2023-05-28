package com.example.shopuserservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class AsyncConfig  {

    public static ConcurrentHashMap<String, Sinks.Many<Object>> sinkMap = new ConcurrentHashMap<>();

    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(30);
        t.setQueueCapacity(10);
        t.setThreadNamePrefix("task-");

        t.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        t.setWaitForTasksToCompleteOnShutdown(true);
        t.setAwaitTerminationSeconds(60);

        t.initialize();
        return t;
    }


}
