package com.issuehub.modules.integrations.application.ports.in;

public record GitHubCallbackCommand(
        String code,
        String developerEmail
) {
}
