package io.blueman.redisdemo;

import io.blueman.redisdemo.utils.RedisDistributedCounter;
import io.blueman.redisdemo.utils.RedisDistributedLock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class RedisDemoApplicationTests {
	@Autowired
	RedisTemplate redisTemplate;
	@BeforeEach
	void setCounter() {
		redisTemplate.opsForValue().set("inventory", 100);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testRedis() {
		redisTemplate.opsForList().leftPush("test_list", "1");
	}

	@Test
	void testCounter() {
		RedisDistributedCounter counter = new RedisDistributedCounter(redisTemplate, "inventory");
		long res = counter.decreaseBy(200);
		assert res == -1;
	}

	@Test
	void testLock() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		threadPool.submit(()->{
			String threadName = "thread1";
			RedisDistributedLock lock = new RedisDistributedLock(redisTemplate, "lock1");
			if (lock.tryLock()) {
				System.out.println("thread:" + threadName + " get lock");
				try {
					Thread.sleep(5_000);
				} catch (InterruptedException e) {
					System.out.println("interrupted ...");
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			} else {
				System.out.println(threadName + " lock1 is locked");
			}
			latch.countDown();
		});

		threadPool.submit(()->{
			String threadName = "thread2";
			RedisDistributedLock lock = new RedisDistributedLock(redisTemplate, "lock1");
			if (lock.tryLock()) {
				System.out.println("thread:" + threadName + " get lock");
				try {
					Thread.sleep(5_000);
				} catch (InterruptedException e) {
					System.out.println("interrupted ...");
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			} else {
				System.out.println(threadName + " lock1 is locked");
			}
			latch.countDown();
		});

		latch.await();
		System.out.println("finished");
	}

}
