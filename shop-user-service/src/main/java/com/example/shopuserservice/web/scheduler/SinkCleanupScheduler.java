package com.example.shopuserservice.web.scheduler;

import com.example.shopuserservice.web.util.reactor.Reactor;
import com.example.shopuserservice.web.util.reactor.SinkStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

/**
 * Every 10 minutes, run removal process start.
 * If sink is not emitting data for a long time, scheduler automatically remove sink object from sinkMap
 */
@Component
public class SinkCleanupScheduler {

    @Scheduled(fixedRate = 600_000)
    public void cleanUpSinks() {
        Reactor.getSinkMap().entrySet().removeIf(entry -> shouldRemoveSink(entry.getValue()));
    }

    private boolean shouldRemoveSink(SinkStream sinkStream) {
        return sinkStream.isExpired();
    }
}