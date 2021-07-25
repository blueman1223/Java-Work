package io.blueman.mqdemo.queuemode;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
public class QueueMqConfig {
    @Bean
    public Queue queue() {
        return new ActiveMQQueue("io.blueman.demo");
    }
}
