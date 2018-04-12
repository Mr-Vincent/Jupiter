package org.jupiter.example.mytest;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/10
 * Time: 11:23
 */
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        Date date = new Date();
        System.out.println(date);
        date.setTime(System.currentTimeMillis() + 20*1000);
        while (true){
            if (System.currentTimeMillis() >= date.getTime()){
                System.out.println(new Date());
                break;
            }
        }
        return "ssss";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyCallable myCallable = new MyCallable();
        FutureTask<String> futureTask = new FutureTask<String>(myCallable);
        Thread thread = new Thread(futureTask);
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();
        System.out.println("waiting for result...");
        futureTask.get();

        if (futureTask.isDone()) {
            String s = futureTask.get();
            System.out.println(s);
        }
        System.out.println("result has got...");

    }
}
