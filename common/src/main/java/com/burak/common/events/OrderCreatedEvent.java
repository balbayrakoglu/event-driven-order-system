package com.burak.common.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        BigDecimal amount,
        LocalDateTime createdAt
) implements BaseEvent {

    @Override
    public EventType type() {
        return EventType.ORDER_CREATED;
    }
}

