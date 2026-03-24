package com.burak.paymentservice.service;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.common.events.PaymentCompletedEvent;
import com.burak.paymentservice.domain.Payment;
import com.burak.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void processPayment(OrderCreatedEvent event) {

        log.info("event=PAYMENT_START orderId={}", event.orderId());

        if (repository.existsByOrderId(event.orderId())) {
            log.warn("event=PAYMENT_DUPLICATE orderId={}", event.orderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.amount())
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        log.info("event=PAYMENT_SUCCESS orderId={}", event.orderId());

        PaymentCompletedEvent completedEvent =
                new PaymentCompletedEvent(
                        event.orderId(),
                        "SUCCESS",
                        LocalDateTime.now()
                );

        kafkaTemplate.send(
                "payment.completed",
                event.orderId().toString(),
                completedEvent
        );
    }
}
