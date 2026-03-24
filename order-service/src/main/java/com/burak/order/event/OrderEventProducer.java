package com.burak.order.event;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(Order order) {

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getAmount(),
                LocalDateTime.now()
        );

        kafkaTemplate.send("order.created", order.getId().toString(), event);

        log.info("event=ORDER_CREATED_SENT orderId={}", order.getId());
    }
}
