package com.burak.paymentservice.service;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.paymentservice.domain.Payment;
import com.burak.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void processPayment(OrderCreatedEvent event) {

        if (repository.existsByOrderId(event.orderId())) {
            return;
        }

        Payment payment = Payment.builder().
                orderId(event.orderId()).
                amount(event.amount()).
                status("SUCCESS").
                createdAt(LocalDateTime.now()).
                build();

        repository.save(payment);
    }
}
