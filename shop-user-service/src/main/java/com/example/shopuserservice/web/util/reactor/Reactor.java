package com.example.shopuserservice.web.util.reactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactor is a class that "manages" the sink.
 * Sink is a class that can emit data to the subscriber.
 *
 * We use Reactor class for emitting data to the subscriber.
 * And managing the sink.
 */
@Configuration
@Slf4j
public class Reactor {
    // create concurrentHashMap for sink storage
    private static final ConcurrentHashMap<String, SinkStream> sinkMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, SinkStream> getSinkMap() {
        return sinkMap;
    }

    // add sinkMap if value is null, else throw exception
    public static void addSink(String key) throws Exception{
        if (sinkMap.get(key) == null) {
            Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
            sinkMap.put(key, new SinkStream(sink));
            log.trace("sink is added");
        }else{
            throw new Exception("sink is not null");
        }
    }

    // remove sinkMap if value is not null, else throw exception
    public static void removeSink(String key) {
        if (sinkMap.get(key) != null) {
            sinkMap.remove(key);
            log.trace("sink is removed");
        }else{
            throw new RuntimeException("sink is null");
        }
    }
    // emit data with complete to sinkMap using key and remove from sinkMap
    public static void emitAndComplete(String key, Object data){
        log.trace("sink try to emit and completed");
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitNext(data);
            sink.tryEmitComplete();
            sinkMap.remove(key);
            log.trace("sink is emitted and completed");
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // get sink from sinkMap using key and transform to flux
    public static Flux<Object> getSink(String key){
        log.trace("try to get sink");
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            return sink.asFlux();
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit data to sinkMap using key
    public static void emit(String key, Object data){
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitNext(data);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit error to sinkMap using key
    public static void emitError(String key, Throwable error){
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitError(error);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit error with complete to sinkMap using key and remove from sinkMap
    public static void emitErrorAndComplete(String key, Throwable error){
        log.trace("emit error with complete to {} and remove from sinkMap", key);
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitError(error);
            sink.tryEmitComplete();
            sinkMap.remove(key);
        }else{
            throw new RuntimeException("sink is null");
        }
    }
}
