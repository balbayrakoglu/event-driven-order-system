package com.burak.order.service;

import com.burak.common.events.OrderCreatedEvent;
import com.burak.order.domain.Order;
import com.burak.order.domain.OrderStatus;
import com.burak.order.outbox.OutboxEvent;
import com.burak.order.outbox.OutboxEventRepository;
import com.burak.order.outbox.OutboxStatus;
import com.burak.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(String product, BigDecimal amount) {

        Order order = Order.builder()
                .product(product)
                .amount(amount)
                .status(OrderStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = repository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getAmount(),
                LocalDateTime.now(),
                "v1"
        );

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(saved.getId())
                    .aggregateType("ORDER")
                    .eventType("ORDER_CREATED")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OrderCreatedEvent", e);
        }

        log.info("event=ORDER_CREATED_DB_SAVED orderId={} amount={}",
                saved.getId(), saved.getAmount());

        return saved;
    }

    @Transactional
    public void markAsPaid(UUID orderId) {
        repository.findById(orderId).ifPresentOrElse(order -> {

            if (order.getStatus() == OrderStatus.PAID) {
                log.info("event=ORDER_ALREADY_PAID orderId={}", orderId);
                return;
            }

            order.setStatus(OrderStatus.PAID);
            repository.save(order);

            log.info("event=ORDER_PAID orderId={}", orderId);

        }, () -> log.warn("event=ORDER_NOT_FOUND orderId={} skipping", orderId));
    }
}