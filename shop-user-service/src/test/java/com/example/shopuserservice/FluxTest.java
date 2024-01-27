package com.example.shopuserservice;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FluxTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    void flux1(){
        // 완전 LAZY로써, subscribe 해서 읽으면 그제서야 가져옴
        Flux<String> source = Flux.fromIterable(Arrays.asList("1", "2")).log();
        source.subscribe(d -> System.out.println("Subscriber 1: "+d));
        source.subscribe(d -> System.out.println("Subscriber 2: "+d));

    }
    @Test
    void subs() throws InterruptedException {


        var a = LocalDateTime.now();
        Flux<String> source = Flux.fromIterable(Arrays.asList("1", "2")).log();



        System.out.println(Duration.between(a, LocalDateTime.now()).toMillis());
    }

    @Test
    void consumer1(){
        // 함수형 프로그래밍
        Consumer<String> c1 = str ->{
            System.out.println(str+" consumer-1");
        };
        Consumer<String> c2 = str ->{
            System.out.println(str+" consumer-2");
        };
        // c1은 소비한 메세지를 c2에 그대로 넘김
        c1.andThen(c2).accept("HI");

        Supplier<String> s1 = ()->{
            System.out.println("supplier-1");
            return "supplier-1";
        };
        s1.get();
    }

    @Test
    void fluxConcatWithValuesTest(){
        // 함수형 프로그래밍
        Flux<String> flux = Flux.just("A", "B");

        Flux<String> fluxWithC = flux.concatWithValues("C");
        Sinks.ManySpec many = Sinks.many();

        flux.subscribe(System.out::println);

        fluxWithC.subscribe(System.out::println);

    }

    @Test
    void flux2(){
        Flux<Integer> f = Flux.just(1, 2, 3, 4, 5)
                .delayElements(Duration.ofSeconds(1))
                .log();
        Sinks.Many<Object> sinks = Sinks.many().multicast().onBackpressureBuffer();
        sinks.tryEmitNext(1);
        f.subscribe(c->{
            System.out.println("Subscriber 1: "+c);
        });
    }

    @Test
    void multiThreadSink(){
        Sinks.Many<Object> sinks = Sinks.many().replay().all();
        ThreadPoolTaskExecutor t = getThreadPoolTaskExecutor();

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            sinks.asFlux().log().subscribe(c -> {
                logger.debug("SUBSCRIBE-task-2: "+c);
            });
            return "Completed";
        },t);
        logger.info("emit 1 start");
        sinks.tryEmitNext("First");
        logger.info("emit 1 end");

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            Flux<Object> flux = sinks.asFlux();
            flux.log().subscribe(c -> {
                logger.debug("SUBSCRIBE-task-3: "+c);
            });
            logger.info("emit 2 start");
            sinks.tryEmitNext("Second"); // task-1 스레드에도 전송
            logger.info("emit 2 end");
            return "Completed";
        },t);
        logger.info("emit 3 start");
        sinks.tryEmitNext("Third");
        logger.info("emit 3 end");
        sinks.asFlux().subscribe(str->{
           logger.info("SUBSCRIBE-task-1: "+(String) str);
        });

        try {
            String s1 = cf1.get();
            System.out.println(s1);
            String s2 = cf2.get();
            System.out.println(s2);
            logger.info("emit 4 start");
            sinks.tryEmitNext("Forth");
            logger.info("emit 4 end");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        t.destroy();
    }
//
//    @Test
//    void sync_non_blocking() throws ExecutionException, InterruptedException {
//        ThreadPoolTaskExecutor t = getThreadPoolTaskExecutor();
//        System.out.println("[Thread 1] - 작업 시작할게요");
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
//            try {Thread.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
//            System.out.println("[Thread 2] - [Thread 1]으로부터 전달받은 작업 처리할게요");
//            return "Thread 2의 결과물";
//        },t);
//
//        // Non-Block!
//        while(!completableFuture.isDone()){
//            Thread.sleep(200);
//            System.out.println("[Thread 1] - Thread 2님 작업이 끝났나요? 그동안 저는 다른일 좀 할게요");
//            System.out.println("[Thread 1] - 다른 일 중...");
//        }
//        // 다음 작업
//        System.out.println("[Thread 1] - 끝났군요! 결과물은 : \""+completableFuture.get()+"\", 이제 다음 작업 수행할게요");
//        System.out.println("[Thread 1] 다음 작업 수행 중...");
//
//        t.destroy();
//    }
//
//    @Test
//    void async_blocking() throws ExecutionException, InterruptedException {
//        ThreadPoolTaskExecutor t = getThreadPoolTaskExecutor();
//        System.out.println("[Thread 1] - 작업 시작할게요");
//
//        // Async
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
//            try {Thread.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
//            System.out.println("[Thread 2] - [Thread 1]으로부터 전달받은 작업 처리할게요");
//            return "Thread 2의 결과물";
//        },t);
//
//        // Blocking
//        String result = completableFuture.get();
//
//        System.out.println("[Thread 1] - 끝났군요! 결과물은 : \""+result+"\", 이제 다음 작업 수행할게요");
//        System.out.println("[Thread 1] 다음 작업 수행 중...");
//
//        t.destroy();
//    }
//    @Test
//    void async_non_blocking() throws ExecutionException, InterruptedException {
//        ThreadPoolTaskExecutor t = getThreadPoolTaskExecutor();
//        Sinks.Many<Object> sinks = Sinks.many().replay().all();
//
//        System.out.println("[Thread 1] - 작업 시작할게요");
//
//        // Async
//        CompletableFuture.runAsync(() -> {
//            try {Thread.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
//            System.out.println("[Thread 2] - [Thread 1]으로부터 전달받은 작업 처리할게요");
//            sinks.tryEmitNext("Thread 2의 결과물");
//        },t);
//
//        // Non-Blocking
//        sinks.asFlux().log().subscribe(result->{
//            System.out.println("[Thread 1] - 끝났군요! 결과물은 : \""+result+"\", 이제 다음 작업 수행할게요");
//        });
//
//        System.out.println("[Thread 1] 다음 작업 수행 중...");
//
//        try {Thread.sleep(1000);} catch (InterruptedException e) {throw new RuntimeException(e);}
//        t.destroy();
//    }

    @Test
    void multiThreadSink2(){
        ConcurrentHashMap<String, Sinks.Many<Object>> sinkMap = new ConcurrentHashMap<>();
        // userId = key
        sinkMap.put("1", Sinks.many().multicast().onBackpressureBuffer());
        sinkMap.put("2", Sinks.many().multicast().onBackpressureBuffer());


        ThreadPoolTaskExecutor t = getThreadPoolTaskExecutor();

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            sinkMap.get("1").asFlux().log().subscribe(c -> {
                logger.debug("SUBSCRIBE-task-1: "+c);
            });
            return "Completed";
        },t);

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            sinkMap.get("2").asFlux().log().subscribe(c -> {
                logger.debug("SUBSCRIBE-task-1: "+c);
            });
            return "Completed";
        },t);

        sinkMap.get("1").tryEmitNext("1");
        sinkMap.get("2").tryEmitNext("2");

        t.destroy();
    }

    @NotNull
    private static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(5);
        t.setMaxPoolSize(10);
        t.setQueueCapacity(10);
        t.setThreadNamePrefix("task-");
        t.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        t.setWaitForTasksToCompleteOnShutdown(true);
        t.initialize();
        return t;
    }


    @Test
    void bifunction(){
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            flux.log().subscribe(str -> {
                System.out.println(str);
            });
        });
        flux.log().subscribe(s->{
            System.out.println(s);
        });
    }

    @Test
    void multithreadFlux(){
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> 10 + i)
                .publishOn(s)
                .map(i -> "value " + i);

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            logger.info("Run CompletableFuture");
            flux.log().subscribe(str -> {
                System.out.println(str);
            });
            return "Completed";
        });

        try {
            cf.get();
            flux.log().subscribe(str -> {
                System.out.println(str);
            });
            System.out.println(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void fromRunnable(){
        Flux<String> flux = Flux.fromIterable(Arrays.asList("a", "b", "c")).log();
        Runnable runnable = () -> logger.info("d");
//        runnable.run();
        Mono<Object> mono = Mono.fromRunnable(() -> {

        });




        flux.subscribe(c->{
            logger.info(c);
        });
        
    }

}
