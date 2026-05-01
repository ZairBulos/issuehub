package com.issuehub.modules.auth.application.ports.in;

import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.shared.domain.models.EntityId;

public record VerifyEmailCommand(
        EntityId developerId,
        VerificationCode code
) {
}
