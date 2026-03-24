package com.burak.common.events;

import java.time.LocalDateTime;

public interface BaseEvent {
    EventType type();

    LocalDateTime createdAt();
}
