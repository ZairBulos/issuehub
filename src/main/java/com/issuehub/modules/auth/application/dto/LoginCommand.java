package com.issuehub.modules.auth.application.dto;

import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;

public record LoginCommand(
        String developerEmail,
        LoginCode code,
        IpAddress ipAddress,
        UserAgent userAgent
) {
}
