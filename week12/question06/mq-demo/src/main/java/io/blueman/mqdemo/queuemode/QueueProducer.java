package io.blueman.mqdemo.queuemode;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
@RequiredArgsConstructor
public class QueueProducer {
    private final Queue demoQueue;
    private final JmsMessagingTemplate jmsMessagingTemplate;

    public void sendMessage(String msg) {
        jmsMessagingTemplate.convertAndSend(demoQueue, msg);
    }
}
