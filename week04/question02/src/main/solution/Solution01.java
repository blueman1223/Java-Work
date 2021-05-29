package main.solution;

import main.Task;

import java.util.concurrent.*;


public class Solution01 {

    public void solute() {
        long start=System.currentTimeMillis();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> fiboFuture = executorService.submit(() -> Task.fibo(36));
        try {
            int result = fiboFuture.get();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
