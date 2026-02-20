package com.issuehub.modules.auth.domain.events;

import com.issuehub.shared.domain.events.DomainEvent;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public record EmailVerified(
        EntityId developerId,
        Instant occurredOn
) implements DomainEvent {
}
