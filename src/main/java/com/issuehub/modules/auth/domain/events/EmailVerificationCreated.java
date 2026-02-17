package com.issuehub.modules.auth.domain.events;

import com.issuehub.shared.domain.events.DomainEvent;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public record EmailVerificationCreated(
        EntityId verificationId,
        EntityId developerId,
        String developerEmail,
        Instant occurredOn
) implements DomainEvent {
}
