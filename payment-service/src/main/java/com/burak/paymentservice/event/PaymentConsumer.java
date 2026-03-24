package com.burak.paymentservice.event;


import com.burak.common.events.OrderCreatedEvent;
import com.burak.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService service;

    @KafkaListener(
            topics = "order.created",
            groupId = "payment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event) {

        log.info("event=ORDER_CREATED_RECEIVED orderId={}", event.orderId());

        service.processPayment(event);
    }
}