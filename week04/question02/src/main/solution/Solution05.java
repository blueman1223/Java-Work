package main.solution;

import main.Task;

public class Solution05 {
    private final Object lock = new Object();
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
            synchronized (lock) {
                new Thread(() -> {
                    setResult(Task.fibo(36));
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }).start();
                lock.wait();
            }
            int result = getResult();
            System.out.println(this.getClass().getName() + "--异步计算结果为："+result);

            System.out.println(this.getClass().getName() + "--使用时间："+ (System.currentTimeMillis()-start) + " ms");
        } catch (InterruptedException e) {
            System.out.println("task interrupted");
        }

    }
}
