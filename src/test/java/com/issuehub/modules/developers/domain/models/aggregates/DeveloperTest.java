package com.issuehub.modules.developers.domain.models.aggregates;

import com.issuehub.modules.developers.domain.exceptions.DeveloperBlockedException;
import com.issuehub.modules.developers.domain.exceptions.DeveloperDeletedException;
import com.issuehub.modules.developers.domain.models.enums.DeveloperStatus;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeveloperTest {

    private Developer developer;

    @BeforeEach
    void setup() {
        developer = Developer.create(
                new DeveloperEmail("test@example.com"),
                DeveloperProfile.defaultProfile()
        );
    }

    // === creation ===
    @Test
    void shouldCreateDeveloper() {
        assertThat(developer.getId()).isNotNull();
        assertThat(developer.getEmail().value()).isEqualTo("test@example.com");
        assertThat(developer.getStatus()).isEqualTo(DeveloperStatus.ACTIVE);
        assertThat(developer.getProfile()).isNotNull();
        assertThat(developer.getVerified()).isFalse();
    }

    // === verification ===
    @Test
    void shouldVerifyDeveloper() {
        developer.verify();
        assertThat(developer.getVerified()).isTrue();
    }

    @Test
    void shouldNotVerifyBlockedDeveloper() {
        developer.block();
        assertThatThrownBy(() -> developer.verify())
                .isInstanceOf(DeveloperBlockedException.class);
    }

    @Test
    void shouldNotVerifyDeletedDeveloper() {
        developer.delete();
        assertThatThrownBy(() -> developer.verify())
                .isInstanceOf(DeveloperDeletedException.class);
    }

    // === blocking ===
    @Test
    void shouldBlockDeveloper() {
        developer.block();
        assertThat(developer.isBlocked()).isTrue();
    }

    // === deletion ===
    @Test
    void shouldDeleteDeveloper() {
        developer.delete();
        assertThat(developer.isDeleted()).isTrue();
    }

    @Test
    void shouldNotDeleteBlockedDeveloper() {
        developer.block();
        assertThatThrownBy(() -> developer.delete())
                .isInstanceOf(DeveloperBlockedException.class);
    }

    // === reactivation ===
    @Test
    void shouldReactivateDeveloper() {
        developer.delete();
        developer.reactivate();
        assertThat(developer.isActive()).isTrue();
    }

    @Test
    void shouldNotReactivateBlockedDeveloper() {
        developer.block();
        assertThatThrownBy(() -> developer.reactivate())
                .isInstanceOf(DeveloperBlockedException.class);
    }

    // === profile update ===
    @Test
    void shouldUpdateDeveloperProfile() {
        DeveloperProfile newProfile = new DeveloperProfile(
                "Juan",
                "ES",
                "Europe/Madrid",
                Map.of("notify_on_ticket_status_change", false)
        );

        developer.updateProfile(newProfile);

        assertThat(developer.getProfile().name()).isEqualTo("Juan");
        assertThat(developer.getProfile().language()).isEqualTo("ES");
        assertThat(developer.getProfile().timezone()).isEqualTo("Europe/Madrid");
    }

    @Test
    void shouldNotUpdateProfileWhenBlocked() {
        developer.block();
        assertThatThrownBy(() -> developer.updateProfile(DeveloperProfile.defaultProfile()))
                .isInstanceOf(DeveloperBlockedException.class);
    }

    @Test
    void shouldNotUpdateProfileWhenDeleted() {
        developer.delete();
        assertThatThrownBy(() -> developer.updateProfile(DeveloperProfile.defaultProfile()))
                .isInstanceOf(DeveloperDeletedException.class);
    }

}
