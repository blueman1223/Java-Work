package io.blueman.redisdemo.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RedisDistributedLock implements Lock {
    public static final long EXPIRE_TIME = 60_000;  // lock超时时间单位ms

    private final RedisTemplate<String, Object> redisTemplate;
    private final String lockName;
    private final Object lockId;

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate, String lockName) {
        this.redisTemplate = redisTemplate;
        this.lockName = lockName;
        this.lockId = UUID.randomUUID();
    }

    @Override
    public void lock() {
        for (;;) {
            if (tryAcquire()) {
                return;
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!tryAcquire()) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public boolean tryLock() {
        return tryAcquire();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long nanoTimeout = unit.toNanos(time);
        long startAt = System.nanoTime();
        boolean acquired = false;
        while (nanoTimeout > System.nanoTime() - startAt
                && !(acquired = tryAcquire())) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
        return acquired;
    }

    @Override
    public void unlock() {
        redisTemplate.execute(RedisScript.of(UNLOCK_SCRIPT_STR, Boolean.class), Collections.singletonList(lockName), lockId);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    private boolean tryAcquire() {
        Boolean res = redisTemplate.opsForValue().setIfAbsent(lockName, lockId, EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return res != null && res;
    }

    private static final String UNLOCK_SCRIPT_STR =
            "if redis.call('get', KEYS[1]) == ARGV[1] then\n\r" +
            "    return redis.call('del', KEYS[1])\n\r" +
            "else\n\r" +
            "    return 0\n\r" +
            "end\n\r";


}
