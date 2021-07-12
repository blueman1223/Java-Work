package io.blueman.redisdemo.service;

import io.blueman.redisdemo.entity.Order;
import io.blueman.redisdemo.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static io.blueman.redisdemo.Constants.ORDER_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final ReactiveStringRedisTemplate redisTemplate;

    public void handle(Order order) {
        log.info("dealing order {}", order.getOrderId());
    }

    // TODO: 可以改成切面 创建成功后向topic publish
    public Order createOrder() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        redisTemplate.convertAndSend(ORDER_TOPIC, JsonUtil.toJson(order));
        return order;
    }
}
