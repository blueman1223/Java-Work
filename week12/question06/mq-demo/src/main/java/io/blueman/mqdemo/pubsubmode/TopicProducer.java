package io.blueman.mqdemo.pubsubmode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Topic;

@Component
@Slf4j
@RequiredArgsConstructor
public class TopicProducer {
    private final Topic demoTopic;
    private final JmsMessagingTemplate jmsMessagingTemplate;

    public void sendTopic(String msg) {
        log.debug("发送Topic消息内容 :{}", msg);
        this.jmsMessagingTemplate.convertAndSend(demoTopic, msg);
    }}
