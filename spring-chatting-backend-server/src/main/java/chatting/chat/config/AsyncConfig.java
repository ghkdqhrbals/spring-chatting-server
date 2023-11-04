package chatting.chat.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(5);
        t.setMaxPoolSize(10);
        t.setQueueCapacity(10);
        t.setThreadNamePrefix("task-");

        t.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        t.setWaitForTasksToCompleteOnShutdown(true);
        t.setAwaitTerminationSeconds(60);

        t.initialize();
        return t;
    }
}
