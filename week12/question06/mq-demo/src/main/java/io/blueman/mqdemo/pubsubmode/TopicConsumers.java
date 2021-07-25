package io.blueman.mqdemo.pubsubmode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Topic;

import static io.blueman.mqdemo.pubsubmode.TopicMqConfig.TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class TopicConsumers {
    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final Topic demoTopic;

    //模拟两个消费者订阅同一个topic producer 发布一条消息，两个订阅者都会接收到
    @JmsListener(destination = TOPIC_NAME, containerFactory = "topicListenerFactory")
    public void consumer1(String msg) {
        log.info("consumer1 received: {}", msg);
    }

    @JmsListener(destination = TOPIC_NAME, containerFactory = "topicListenerFactory")
    public void consumer2(String msg) {
        log.info("consumer2 received: {}", msg);
    }
}
