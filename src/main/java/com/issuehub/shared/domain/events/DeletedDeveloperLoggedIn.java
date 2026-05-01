package com.issuehub.shared.domain.events;

import com.issuehub.shared.domain.models.EntityId;

import java.time.Instant;

public record DeletedDeveloperLoggedIn(
        EntityId developerId,
        Instant occurredOn
) implements DomainEvent {
}
