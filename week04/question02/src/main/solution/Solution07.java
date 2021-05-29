package main.solution;

import main.Task;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Solution07 {
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
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
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.out.println("task sync failed");
                }
            }).start();
            cyclicBarrier.await();
            int result = getResult();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        } catch (BrokenBarrierException e) {
            System.out.println("task barrier has broken");
        }

    }
}
