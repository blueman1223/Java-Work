package io.blueman.kafkademo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Producer {
    private static final String TOPIC = "testTopic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        this.kafkaTemplate.send(TOPIC, message);
        log.info("send message:{} to topic {}", message, TOPIC);
    }
}
