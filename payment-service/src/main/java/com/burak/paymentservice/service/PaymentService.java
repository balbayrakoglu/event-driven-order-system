package com.burak.paymentservice.service;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.common.events.PaymentCompletedEvent;
import com.burak.common.events.PaymentStatus;
import com.burak.paymentservice.domain.Payment;
import com.burak.paymentservice.outbox.OutboxEvent;
import com.burak.paymentservice.outbox.OutboxEventRepository;
import com.burak.paymentservice.outbox.OutboxStatus;
import com.burak.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository repository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPayment(OrderCreatedEvent event) {
        log.info("event=PAYMENT_START orderId={} eventId={}", event.orderId(), event.eventId());

        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                UUID.randomUUID(),
                event.orderId(),
                PaymentStatus.SUCCESS,
                LocalDateTime.now(),
                "v1"
        );

        try {
            Payment payment = Payment.builder()
                    .orderId(event.orderId())
                    .amount(event.amount())
                    .status(PaymentStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.saveAndFlush(payment);

            saveOutboxEvent(event.orderId(), paymentCompletedEvent);

            log.info("event=PAYMENT_SUCCESS orderId={}", event.orderId());

        } catch (DataIntegrityViolationException ex) {
            log.warn("event=PAYMENT_DUPLICATE orderId={} reason=unique_constraint", event.orderId());

            saveOutboxEvent(event.orderId(), paymentCompletedEvent);
        }
    }

    private void saveOutboxEvent(UUID aggregateId, PaymentCompletedEvent event) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(aggregateId)
                    .aggregateType("PAYMENT")
                    .eventType("PAYMENT_COMPLETED")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .retryCount(0)
                    .nextRetryAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outboxEvent);

            log.info("event=OUTBOX_SAVED eventType={} aggregateId={}",
                    outboxEvent.getEventType(), outboxEvent.getAggregateId());

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PaymentCompletedEvent", e);
        }
    }
}