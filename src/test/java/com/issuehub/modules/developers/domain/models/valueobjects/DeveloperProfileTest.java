package com.issuehub.modules.developers.domain.models.valueobjects;

import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperProfileException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeveloperProfileTest {

    @Test
    void shouldCreateValidProfileAndNormalizeValues() {
        var profile = new DeveloperProfile(
                "John",
                "en",
                " America/New_York ",
                Map.of("notify_on_ticket_creation", false)
        );

        assertThat(profile.name()).isEqualTo("John");
        assertThat(profile.language()).isEqualTo("EN");
        assertThat(profile.timezone()).isEqualTo("America/New_York");
        assertThat(profile.notificationPreferences()).containsEntry("notify_on_ticket_creation", false);
    }

    @Test
    void shouldCreateValidDefaultProfile() {
        var profile = DeveloperProfile.defaultProfile();

        assertThat(profile.name()).isEqualTo(null);
        assertThat(profile.language()).isEqualTo("ES");
        assertThat(profile.timezone()).isEqualTo("America/Argentina/Buenos_Aires");
        assertThat(profile.notificationPreferences()).containsEntry("notify_on_ticket_creation", true);
        assertThat(profile.notificationPreferences()).containsEntry("notify_on_ticket_status_change", true);
    }

    @Test
    void shouldThrowExceptionWhenLanguageIsBlank() {
        assertThatThrownBy(() -> new DeveloperProfile(
                "John",
                "",
                "America/New_York",
                Map.of("notify_on_ticket_creation", false))
        ).isInstanceOf(InvalidDeveloperProfileException.class);
    }

    @Test
    void shouldThrowExceptionWhenTimezoneIsBlank() {
        assertThatThrownBy(() -> new DeveloperProfile(
                "John",
                "EN",
                "",
                Map.of("notify_on_ticket_creation", false))
        ).isInstanceOf(InvalidDeveloperProfileException.class);
    }

    @Test
    void shouldThrowExceptionWhenPreferencesAreEmpty() {
        assertThatThrownBy(() -> new DeveloperProfile(
                "John",
                "EN",
                "America/New_York",
                Map.of())
        ).isInstanceOf(InvalidDeveloperProfileException.class);
    }

}
