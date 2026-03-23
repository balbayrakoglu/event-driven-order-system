package com.burak.paymentservice.event;


import com.burak.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.burak.paymentservice.service.PaymentService;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService service;

    @KafkaListener(
            topics = "order.created",
            groupId = "payment-group"
    )
    public void consume(OrderCreatedEvent event) {
       service.processPayment(event);
    }
}
