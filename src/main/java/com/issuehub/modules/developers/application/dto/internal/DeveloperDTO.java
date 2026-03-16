package com.issuehub.modules.developers.application.dto.internal;

import com.issuehub.modules.developers.domain.models.aggregates.Developer;

import java.util.Map;
import java.util.UUID;

public record DeveloperDTO(
        UUID id,
        String email,
        String name,
        String language,
        String timezone,
        Map<String, Boolean> notificationPreferences
) {

    public static DeveloperDTO from(Developer developer) {
        return new DeveloperDTO(
                developer.getId().value(),
                developer.getEmail().value(),
                developer.getProfile().name(),
                developer.getProfile().language(),
                developer.getProfile().timezone(),
                developer.getProfile().notificationPreferences()
        );
    }

}
