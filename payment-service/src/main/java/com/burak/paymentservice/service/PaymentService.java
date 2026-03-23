package com.burak.paymentservice.service;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.paymentservice.domain.Payment;
import com.burak.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    public void processPayment(OrderCreatedEvent event) {

        if (repository.existsByOrderId(event.orderId())) {
            System.out.println("Duplicate event ignored: " + event.orderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.amount())
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        System.out.println("Payment created for order: " + event.orderId());
    }
}
