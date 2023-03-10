package chatting.chat.web.error;

import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public class HandlingExecutor implements AsyncTaskExecutor {
    private AsyncTaskExecutor executor;

    public HandlingExecutor(AsyncTaskExecutor executor) {
        System.out.println("init@");
        this.executor = executor;
    }

    @Override
    public void execute(Runnable task) {
        System.out.println("execute1");
        executor.execute(task);
        System.out.println("execute1-1");
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        System.out.println("execute2");
        executor.execute(createWrappedRunnable(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        System.out.println("submit1");
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        System.out.println("submit2");
        return executor.submit(createCallable(task));
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    System.out.println("RUN IN TRHEAD");
                    return task.call();
                } catch (Exception e) {
                    System.out.println("CATCH EXECPTIONS!");
                    handle(e);
                    throw e;
                }
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("RUN IN TRHEAD");
                    task.run();
                } catch (Exception e) {
                    System.out.println("CATCH EXECPTIONS!");
                    handle(e);
                }
            }
        };
    }

    private void handle(Exception e) {
        System.out.println(e.toString()+"COUATAUTUWETUAWETUADGFADSB!!!!!!!!!");
    }
}