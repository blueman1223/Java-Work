package io.blueman.mqdemo.queuemode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueConsumer {
    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final Queue demoQueue;

    public String receiveMessage() throws InterruptedException {
        String msg;

        //默认无限等待，可以通过JmsTemplate().setReceiveTimeout(long);修改行为
        msg = jmsMessagingTemplate.receiveAndConvert(demoQueue, String.class);

        return msg;
    }
}
