package com.burak.order.service;

import com.burak.order.domain.Order;
import com.burak.order.domain.OrderStatus;
import com.burak.order.event.OrderEventProducer;
import com.burak.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventProducer producer;

    public Order createOrder(String product, BigDecimal amount) {

        Order order = Order.builder()
                .product(product)
                .amount(amount)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = repository.save(order);

        producer.sendOrderCreated(saved);

        return saved;
    }

    public void markAsPaid(final UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow();

        order.setStatus(OrderStatus.PAID);
        repository.save(order);
    }
}
