package com.issuehub.modules.auth.application.dto.internal;

import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.shared.domain.model.EntityId;

public record VerifyEmailCommand(
        EntityId developerId,
        VerificationCode code
) {
}
