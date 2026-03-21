package com.issuehub.modules.integrations.application.dto;

public record GitHubCallbackCommand(
        String code,
        String state,
        String developerEmail
) {
}
