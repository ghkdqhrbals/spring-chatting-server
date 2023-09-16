package com.example.shopuserservice.web.util.reactor;

import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;

public class SinkStream {
    // sink is a class that can emit data to the subscriber.
    private Sinks.Many<Object> sink;

    // time when sink is created
    // using this time, we can check how long it takes to emit data to the subscriber.
    // and scheduler can check if the sink is not emitting data for a long time.
    private LocalDateTime createdAt;
}
