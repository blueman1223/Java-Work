package io.kimmking.kmq.core;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue<E> {
    private final List<E> list;
    private final AtomicInteger rear; //队尾指针
    private final AtomicInteger front;    //队首指针

    /** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    public MyQueue() {
        this.list = new ArrayList<>();
        this.rear = new AtomicInteger();
        this.front = new AtomicInteger();
    }

    public MyQueue(int capacity) {
        this.list = new ArrayList<>(capacity);
        this.rear = new AtomicInteger();
        this.front = new AtomicInteger();
    }

    public int size() {
        return rear.get() - front.get();
    }

    public boolean isEmpty() {
        return rear.get() == front.get();
    }

    public boolean offer(E e) {
        try {
            putLock.lock();
            list.add(e);
            rear.incrementAndGet();
        } finally {
            putLock.unlock();
        }
        return true;
    }

    public E poll() {
        try {
            takeLock.lock();
            if (isEmpty()) return null;
            int nowFront = front.get();
            E ele = list.get(nowFront);
            front.incrementAndGet();
            return ele;
        } finally {
            takeLock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E ele = null;
        long nanos = unit.toNanos(timeout);
        takeLock.lockInterruptibly();
        try {
            //自旋等待
            while (isEmpty()) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            ele = poll();
        } finally {
            takeLock.unlock();
        }
        return ele;
    }

}
