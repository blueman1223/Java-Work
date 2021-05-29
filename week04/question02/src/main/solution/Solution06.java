package main.solution;

import main.Task;

import java.util.concurrent.CountDownLatch;

public class Solution06 {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
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
            new Thread(() -> {
                setResult(Task.fibo(36));
                countDownLatch.countDown();
            }).start();
            countDownLatch.await();
            int result = getResult();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        }

    }
}
