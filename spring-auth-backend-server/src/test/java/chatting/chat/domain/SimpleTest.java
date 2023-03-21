package chatting.chat.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SimpleTest {
    static int staticVariable = 0;

    @FunctionalInterface
    interface MyLambdaFunction {
        String max(String a, String b);
    }

    @Test
    void test1() {

        System.out.println(Thread.currentThread().getName()+" 시작");
        ExecutorService executor = Executors.newFixedThreadPool(5);

        int localVariable = 0;
        String aa = "222";
        List<Integer> numbers_not_thread_safe = Arrays.asList(1, 2, 3);

        Map<Integer,Integer> maps_not_thread_safe = new HashMap<>(){{
            put(0,0);
            put(1,2);
            put(2,3);
        }};

        List<Integer> numbers_thread_safe = Collections.synchronizedList(new ArrayList<Integer>());
        numbers_thread_safe.add(1);
        numbers_thread_safe.add(2);
        numbers_thread_safe.add(3);

        ConcurrentMap<Integer,Integer> maps_thread_safe = new ConcurrentHashMap<>(){{
            put(0,0);
            put(1,2);
            put(2,3);
        }};

        try {
            String s = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName()+" 시작");
                // ------ NOT THREAD SAFE
                // 시작 이후, 람다는 지역 변수(localVariable)를 "capturing" 해서 가지고 있도록 지원합니다. 즉, 스레드 stack 에 새롭게 쌓아 올린 것입니다.
                // 그리고 우리는 여기서 새로운 스레드로 표현하였죠. 그렇다면, 새로운 스레드의 스택에 해당 지역 변수가 복사됩니다.
                // 하지만 Java는 지역변수 수정(지역 변수을 다른 메소드에서 변경)을 미연에 방지하고자 컴파일 단계에서 에러를 띄웁니다.
                // 따라서 새로운 스레드에서 캡처링 된 지역 변수를 이리저리 바꾸기 위해서는, 따로 지역변수를 받아서 쓰거나
                // 애초에 지역적으로 선언하지 말고 힙에 저장되도록 Object로 선언해야 합니다.

                // localVariable += 1; // error 발생 > capturing 한 변수에 대한 변경 권한이 없습니다.

                var newLocalVariable= localVariable; // 람다식 내 새로운 변수로 할당합니다.
                newLocalVariable += 1;

                staticVariable += 1;
                numbers_not_thread_safe.set(0, 1);
                maps_not_thread_safe.put(0,1);

                // ------ THREAD SAFE
                numbers_thread_safe.set(0,1);
                maps_thread_safe.put(0,1);

                // 대신 모두 읽기만 가능
                System.out.println("not-thread-safe-localVariable=" + String.valueOf(localVariable));
                System.out.println("not-thread-safe-newlocalVariable=" + String.valueOf(newLocalVariable));
                System.out.println("not-thread-safe-staticVariable=" + String.valueOf(staticVariable));
                System.out.println("not-thread-safe-list=" + String.valueOf(numbers_not_thread_safe.get(0)));
                System.out.println("not-thread-safe-map=" + String.valueOf(maps_not_thread_safe.get(0)));
                System.out.println("thread-safe-list=" + String.valueOf(numbers_thread_safe.get(0)));
                System.out.println("thread-safe-map=" + String.valueOf(maps_thread_safe.get(0)));

            }, executor).thenApplyAsync((e1) -> {
                return "Finished";
            }, executor).get();
            System.out.println(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void test2() {
        String oldString = "oldString";
        String raplaceWith="newString";
        System.out.println(oldString.hashCode());
        System.out.println(raplaceWith.hashCode());
        String newString = new MyLambdaFunction() {
            public String max(String a, String b) {
                System.out.println(a.hashCode());
                System.out.println(b.hashCode());
                a = b;

                // String 은 불변객체임으로, 수정이 일어난다면 새로운 String 값으로 반환합니다.
                // 즉, 기존 주소값을 참조해서 수정하고 전달하는 것이 아닌, 새롭게 만들어서 반환하는 것입니다.
                //
                // (Integer, Double, Character, BigInteger 같은 모든 숫자형 클래스들도 불변)
                a = a.replace("new", "");
                System.out.println(System.identityHashCode(a));
                System.out.println(System.identityHashCode(b));
                return a;
            }
        }.max(oldString, raplaceWith);

        System.out.println(oldString +", "+newString);
        System.out.println(System.identityHashCode(oldString));
        System.out.println(System.identityHashCode(raplaceWith));
        System.out.println(System.identityHashCode(newString));

        // String 은 불변객체임으로, 수정이 일어난다면 새로운 String 값으로 반환합니다.
        // 즉, 기존 주소값을 참조해서 수정하고 전달하는 것이 아닌, 새롭게 만들어서 반환하는 것입니다.
        // (Integer, Double, Character, BigInteger 같은 모든 숫자형 클래스들도 불변)
        // 사실 JVM GC로 인해 정확한 주소값을 참조할 순 없으나 그와 유사하게 identityHashCode 로 확인할 순 있습니다.

        String t1 = "oldString";
        String t2 = "oldString";
        System.out.println("address t1:"+System.identityHashCode(t1)+", "+t1); // 동일
        System.out.println("address t2:"+System.identityHashCode(t2)+", "+t1); // 동일

        t1 += "+newString";
        System.out.println("address new t1R:"+System.identityHashCode(t1)+", "+t1); // 다름




//        System.out.println(Integer.toHexString(s1.hashCode()));
//        System.out.println(Integer.toHexString(s2.hashCode()));
//        System.out.println(Integer.toHexString(s3.hashCode()));
//        System.out.println(System.identityHashCode(s1));
//        System.out.println(System.identityHashCode(s2));
//        System.out.println(System.identityHashCode(s3));

    }

    @Test
    public void threadTest(){
        class WorkerThread implements Runnable {

            private String command;

            public WorkerThread(String s){
                this.command=s;
            }

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" Start. Command = "+command);
                processCommand();
                System.out.println(Thread.currentThread().getName()+" End.");
            }

            private void processCommand() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString(){
                return this.command;
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new WorkerThread("" + i);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
    }
}
