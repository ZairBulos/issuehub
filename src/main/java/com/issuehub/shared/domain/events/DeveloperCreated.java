package com.issuehub.shared.domain.events;

import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public record DeveloperCreated(
        EntityId developerId,
        String developerEmail,
        Instant occurredOn
) implements DomainEvent {
}
