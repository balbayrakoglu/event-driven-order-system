package com.burak.paymentservice.event;


import com.burak.common.events.OrderCreatedEvent;
import com.burak.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
        service.processPayment(event);
    }
}
