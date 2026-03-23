package com.burak.order.controller.dto;

import com.burak.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String product,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt) {
}
