package io.blueman.redisdemo.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;

@RequiredArgsConstructor
public class RedisDistributedCounter {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final String counterName;

    public long decreaseBy(long amount) {
        Long res = redisTemplate.execute(RedisScript.of(DECREASE_SCRIPT_STR, Long.class), Collections.singletonList(counterName), amount);
        if (res == null) {
            throw new RuntimeException("decrease return null");
        }
        return res;
    }

    private static final String DECREASE_SCRIPT_STR =
            "local amount = redis.call('get', KEYS[1])\n\r" +
            "if tonumber(amount) >= tonumber(ARGV[1]) then\n\r" +
                    "    return redis.call('DECRBY', KEYS[1], tonumber(ARGV[1]))\n\r" +
                    "else\n\r" +
                    "    return -1\n\r" +
                    "end\n\r";
}
