package main.solution;

import main.Task;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Solution09 {
    private volatile int result;
    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void solute() {
        long start=System.currentTimeMillis();
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();

        try {
            reentrantLock.lock();   // 防止 signal先于await调用
            new Thread(() -> {
                setResult(Task.fibo(36));
                reentrantLock.lock();
                condition.signal();
                reentrantLock.unlock();
            }).start();
            condition.await();  // 自动释放reentrantLock
            int result = getResult();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        }

    }
}
