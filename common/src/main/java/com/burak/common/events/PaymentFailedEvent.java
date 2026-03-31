package com.burak.common.events;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID orderId,
        String reason
) {
}
