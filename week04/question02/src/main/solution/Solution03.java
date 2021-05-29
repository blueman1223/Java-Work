package main.solution;

import main.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Solution03 {
    private volatile int result;
    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void solute() {
        long start=System.currentTimeMillis();

        try {
            Thread thread = new Thread(()->setResult(Task.fibo(36)));
            thread.start();
            thread.join();  // 等待thread执行完毕
            int result = getResult();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        }

    }
}
