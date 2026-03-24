package com.burak.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(UUID orderId, String status, LocalDateTime createdAt) implements BaseEvent {

    @Override
    public EventType type() {
        return EventType.PAYMENT_COMPLETED;
    }
}