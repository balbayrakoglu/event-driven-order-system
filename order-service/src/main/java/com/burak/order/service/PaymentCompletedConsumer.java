package com.burak.order.service;

import com.burak.common.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "order-group"
    )
    public void consume(PaymentCompletedEvent event) {
        orderService.markAsPaid(event.orderId());
    }
}