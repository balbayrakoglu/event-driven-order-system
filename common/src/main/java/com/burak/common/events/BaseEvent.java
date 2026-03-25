package com.burak.common.events;

import java.time.LocalDateTime;
import java.util.UUID;


public interface BaseEvent {

    UUID eventId();

    EventType type();

    LocalDateTime createdAt();

    String version();
}
