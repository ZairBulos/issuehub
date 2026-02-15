package com.issuehub.modules.developers.domain.models.valueobjects;

import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperProfileException;

import java.util.Map;
import java.util.Objects;

public record DeveloperProfile(
        String name,
        String language,
        String timezone,
        Map<String, Boolean> notificationPreferences
) {

    public DeveloperProfile {
        Objects.requireNonNull(language, "Developer language cannot be null");
        Objects.requireNonNull(timezone, "Developer timezone cannot be null");
        Objects.requireNonNull(notificationPreferences, "Developer preferences cannot be null");

        if (language.isBlank())
            throw new InvalidDeveloperProfileException("Developer language cannot be blank");
        if (timezone.isBlank())
            throw new InvalidDeveloperProfileException("Developer timezone cannot be blank");
        if (notificationPreferences.isEmpty())
            throw new InvalidDeveloperProfileException("Developer preferences cannot be empty");

        name = (name != null && !name.isBlank()) ? name.trim() : null;
        language = language.trim().toUpperCase();
        timezone = timezone.trim();
        notificationPreferences = Map.copyOf(notificationPreferences);
    }

    public static DeveloperProfile defaultProfile() {
        return new DeveloperProfile(
                null,
                "ES",
                "America/Argentina/Buenos_Aires",
                Map.of(
                        "notify_on_ticket_creation", true,
                        "notify_on_ticket_status_change", true
                )
        );
    }

}
