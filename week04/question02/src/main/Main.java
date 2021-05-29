package main;

import main.solution.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        new Solution01().solute();  // ExecutorService.submit
        new Solution02().solute();  // FutureTask.run
        new Solution03().solute(); // Thread.join
        new Solution04().solute(); // Thread.sleep 不能保证获取正确结果
        new Solution05().solute(); // Object.wait&notify
        new Solution06().solute(); // CountdownLatch.await&countdown
        new Solution07().solute(); // CyclicBarrier.await
        new Solution08().solute(); // Semaphore.acquire
        new Solution09().solute(); // ReentrantLock Condition await&release
    }


}
