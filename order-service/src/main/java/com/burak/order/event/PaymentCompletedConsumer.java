package com.burak.order.event;

import com.burak.common.events.PaymentCompletedEvent;
import com.burak.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-completed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(PaymentCompletedEvent event) {
        log.info("event=PAYMENT_COMPLETED_RECEIVED orderId={}", event.orderId());
        try {
            orderService.markAsPaid(event.orderId());
        } catch (NoSuchElementException e) {
            log.warn("event=PAYMENT_COMPLETED_ORDER_NOT_FOUND orderId={} — skipping", event.orderId());
        }
    }
}
