package com.issuehub.modules.developers.application.ports.in.internal;

import com.issuehub.modules.developers.domain.models.aggregates.Developer;

import java.util.Map;
import java.util.UUID;

public record DeveloperDetails(
        UUID id,
        String email,
        String name,
        String language,
        String timezone,
        Map<String, Boolean> notificationPreferences
) {

    public static DeveloperDetails from(Developer developer) {
        return new DeveloperDetails(
                developer.getId().value(),
                developer.getEmail().value(),
                developer.getProfile().name(),
                developer.getProfile().language(),
                developer.getProfile().timezone(),
                developer.getProfile().notificationPreferences()
        );
    }

}
