package io.blueman.redisdemo.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueman.redisdemo.entity.Order;
import io.blueman.redisdemo.service.OrderService;
import static io.blueman.redisdemo.Constants.ORDER_TOPIC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {
    private final OrderService orderService;
    private final ReactiveStringRedisTemplate reactiveRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        subscribeOrderMessage();
    }

    public void subscribeOrderMessage() {
        reactiveRedisTemplate.listenTo(PatternTopic.of(ORDER_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .map(this::convert)
                .filter(order -> order != null && order.getOrderId() != null)
                .subscribe(orderService::handle);
    }

    private Order convert(String orderJson) {
        try {
            return objectMapper.readValue(orderJson, Order.class);
        } catch (JsonProcessingException e) {
            log.error("convert {} to Order failed filed", orderJson, e);
            return null;
        }
    }

}
