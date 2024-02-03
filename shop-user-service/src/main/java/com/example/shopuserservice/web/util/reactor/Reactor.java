package com.example.shopuserservice.web.util.reactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 편하게 내부 {@link SinkStream} 을 관리하기 위해 만든 클래스입니다.
 * <br>
 * Reactor is a class that "manages" the sink.
 * Sink is a class that can emit data to the subscriber.
 *
 * We use Reactor class for emitting data to the subscriber.
 * And managing the sink.
 *
 *
 */
@Configuration
@Slf4j
public class Reactor {

    /**
     * {@link SinkStream} 을 저장하는 해시맵으로써, 클라이언트에게 데이터를 emit 하기 위해 사용합니다.
     */
    private static final ConcurrentHashMap<String, SinkStream> sinkMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, SinkStream> getSinkMap() {
        return sinkMap;
    }

    /**
     * {@link SinkStream} 를 ConcurrentHashMap 에 추가합니다.
     * @param key
     * @throws Exception
     */
    public static void addSink(String key) throws Exception{
        if (sinkMap.get(key) == null) {
            Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
            sinkMap.put(key, new SinkStream(sink));
            log.trace("sink is added");
        }else{
            throw new Exception("sink is not null");
        }
    }

    /**
     * {@link SinkStream} 를 ConcurrentHashMap 에서 제거합니다.
     * @param key
     */
    public static void removeSink(String key) {
        if (sinkMap.get(key) != null) {
            sinkMap.remove(key);
            log.trace("sink is removed");
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    /**
     * ConcurrentHashMap 에 저장된 {@link SinkStream} 를 가져와서 data 를 emit 한 뒤 sinkMap 에서 제거합니다.
     * @param key
     * @param data
     */
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

    /**
     * ConcurrentHashMap 에 저장된 {@link SinkStream} 를 가져와서 {@link Flux} 로 변환합니다.
     * @param key
     * @return Flux
     */
    public static Flux<Object> getSink(String key){
        log.trace("try to get sink");
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            return sink.asFlux();
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    /**
     * ConcurrentHashMap 에 저장된 {@link SinkStream} 에 data 를 emit 합니다.
     * @param key
     * @param data
     */
    public static void emit(String key, Object data){
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitNext(data);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    /**
     * ConcurrentHashMap 에 저장된 {@link SinkStream} 에 error 를 emit 합니다.
     * @param key
     * @param error
     */
    public static void emitError(String key, Throwable error){
        Sinks.Many<Object> sink = sinkMap.get(key).getSink();
        if (sink != null) {
            sink.tryEmitError(error);
        }else{
            throw new RuntimeException("sink is null");
        }
    }

    /**
     * ConcurrentHashMap 에 저장된 {@link SinkStream} 에 error 를 emit 한 뒤 SinkStream 종료시키고 sinkMap 에서 제거합니다.
     * @param key
     * @param error
     */
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
