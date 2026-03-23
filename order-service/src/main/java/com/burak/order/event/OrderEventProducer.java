package com.burak.order.event;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(final Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getAmount()
        );

        kafkaTemplate.send(
                "order.created",
                order.getId().toString(),
                event
        );
    }
}
