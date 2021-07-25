package io.blueman.mqdemo;

import io.blueman.mqdemo.queuemode.QueueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ConsumerTest {
	@Autowired
	QueueConsumer queueConsumer;

	@Test
	void testReceive() throws InterruptedException {
		String msg = queueConsumer.receiveMessage();
		assert msg != null;
		log.debug("received {}", msg);
	}

}
