package com.burak.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID orderId,
        PaymentStatus status,
        LocalDateTime createdAt,
        String version
) implements BaseEvent {

    @Override
    public EventType type() {
        return EventType.PAYMENT_COMPLETED;
    }
}