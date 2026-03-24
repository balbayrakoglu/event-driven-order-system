package com.burak.order.service;

import com.burak.common.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "order-group"
    )
    public void consume(PaymentCompletedEvent event) {

        log.info("event=PAYMENT_COMPLETED_RECEIVED orderId={}", event.orderId());

        orderService.markAsPaid(event.orderId());
    }
}