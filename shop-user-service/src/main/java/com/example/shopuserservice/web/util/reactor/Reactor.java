package com.example.shopuserservice.web.util.reactor;

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
public class Reactor {
    // create concurrentHashMap for sink storage
    private static final ConcurrentHashMap<String, Sinks.Many<Object>> sinkMap = new ConcurrentHashMap<>();

    // add sinkMap if value is null, else throw exception
    public static void addSink(String key) throws Exception{
        if (sinkMap.get(key) == null) {
            Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
            sinkMap.put(key, sink);
        }else{
            throw new Exception("sink is not null");
        }
    }

    // remove sinkMap if value is not null, else throw exception
    public static void removeSink(String key) {
        if (sinkMap.get(key) != null) {
            sinkMap.remove(key);
        }else{
            throw new RuntimeException("sink is null");
        }
    }
    // emit data with complete to sinkMap using key and remove from sinkMap
    public static void emitAndComplete(String key, Object data){
        Sinks.Many<Object> sink = sinkMap.get(key);
        if (sink != null) {
            sink.tryEmitNext(data);
            sink.tryEmitComplete();
            sinkMap.remove(key);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // get sink from sinkMap using key and transform to flux
    public static Flux<Object> getSink(String key){
        Sinks.Many<Object> sink = sinkMap.get(key);
        if (sink != null) {
            return sink.asFlux();
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit data to sinkMap using key
    public static void emit(String key, Object data){
        Sinks.Many<Object> sink = sinkMap.get(key);
        if (sink != null) {
            sink.tryEmitNext(data);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit error to sinkMap using key
    public static void emitError(String key, Throwable error){
        Sinks.Many<Object> sink = sinkMap.get(key);
        if (sink != null) {
            sink.tryEmitError(error);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    // emit error with complete to sinkMap using key and remove from sinkMap
    public static void emitErrorAndComplete(String key, Throwable error){
        Sinks.Many<Object> sink = sinkMap.get(key);
        if (sink != null) {
            sink.tryEmitError(error);
            sink.tryEmitComplete();
            sinkMap.remove(key);
        }else{
            throw new RuntimeException("sink is null");
        }
    }
}
