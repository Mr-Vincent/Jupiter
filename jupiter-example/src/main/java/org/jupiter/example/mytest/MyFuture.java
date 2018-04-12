package org.jupiter.example.mytest;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/10
 * Time: 10:23
 */
public class MyFuture {

    private static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(0, 4,
            1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r,"MyFuture");
        }
    });

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Future<?> futureRunnable = poolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("hahahah");
            }
        },"result");

        Future<String> futureCallable = poolExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "yes";
            }
        });

        System.out.println("futureRunnable result == "+futureRunnable.get());

        System.out.println("futureCallable result == "+futureCallable.get());
        poolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("[current thread name:" + Thread.currentThread().getName() + "] MyFuture");
            }
        });
        poolExecutor.shutdown();
    }

}
