package io.blueman.mqdemo;

import io.blueman.mqdemo.pubsubmode.TopicProducer;
import io.blueman.mqdemo.queuemode.QueueConsumer;
import io.blueman.mqdemo.queuemode.QueueProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProducerTest {
	@Autowired
	QueueProducer queueProducer;

	@Autowired
	TopicProducer topicProducer;

	@Test
	void testSend() throws InterruptedException {
		queueProducer.sendMessage("hello consumer");
	}

	@Test
	void testTopicSend() {
		topicProducer.sendTopic("hello consumer");
	}

}
