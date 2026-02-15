package com.issuehub.shared.domain.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
