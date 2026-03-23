package com.burak.order.service;

import com.burak.order.domain.Order;
import com.burak.order.domain.OrderStatus;
import com.burak.order.event.OrderEventProducer;
import com.burak.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderEventProducer orderEventProducer;

    public Order createOrder(final String product, final BigDecimal amount) {

        Order order = Order.builder()
                .product(product)
                .amount(amount)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
        Order saved = orderRepository.save(order);

        orderEventProducer.sendOrderCreatedEvent(saved);

        return saved;
    }

}
