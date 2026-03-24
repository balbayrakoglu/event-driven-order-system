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

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void processPayment(OrderCreatedEvent event) {

        log.info("Processing payment for orderId={}", event.orderId());

        if (repository.existsByOrderId(event.orderId())) {
            log.warn("Duplicate payment ignored for orderId={}", event.orderId());

            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.amount())
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        log.info("Payment completed for orderId={}", event.orderId());

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
