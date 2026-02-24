package com.issuehub.modules.auth.domain.events;

import com.issuehub.shared.domain.events.DomainEvent;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public record LoginVerificationCreated(
        EntityId verificationId,
        EntityId developerId,
        String developerEmail,
        String verificationCode,
        Instant occurredOn
) implements DomainEvent {
}
