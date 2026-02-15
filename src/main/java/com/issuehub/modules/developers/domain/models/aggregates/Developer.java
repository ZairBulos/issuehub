package com.issuehub.modules.developers.domain.models.aggregates;

import com.issuehub.modules.developers.domain.exceptions.DeveloperBlockedException;
import com.issuehub.modules.developers.domain.exceptions.DeveloperDeletedException;
import com.issuehub.modules.developers.domain.models.enums.DeveloperStatus;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.shared.domain.model.AggregateRoot;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public class Developer extends AggregateRoot {

    private final EntityId id;
    private final DeveloperEmail email;
    private Boolean isVerified;
    private DeveloperStatus status;
    private DeveloperProfile profile;
    private final Instant createdAt;
    private Instant updatedAt;

    // === Factory Method ===
    public static Developer create(DeveloperEmail email, DeveloperProfile profile) {
        return new Developer(
                EntityId.generate(),
                email,
                false,
                DeveloperStatus.ACTIVE,
                profile != null ? profile : DeveloperProfile.defaultProfile(),
                Instant.now(),
                Instant.now()
        );
    }

    // === Constructor ===
    public Developer(EntityId id, DeveloperEmail email, Boolean isVerified, DeveloperStatus status, DeveloperProfile profile, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.isVerified = isVerified;
        this.status = status;
        this.profile = profile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // === State cheks ===
    public boolean isActive() {
        return status == DeveloperStatus.ACTIVE;
    }

    public boolean isBlocked() {
        return status == DeveloperStatus.BLOCKED;
    }

    public boolean isDeleted() {
        return status == DeveloperStatus.DELETED;
    }

    // === Actions ===
    public void verify() {
        if (isVerified)
            return;

        if (isBlocked())
            throw new DeveloperBlockedException(this.id);

        if (isDeleted())
            throw new DeveloperDeletedException(this.id);

        this.isVerified = true;
        this.updatedAt = Instant.now();
    }

    public void block() {
        if (isBlocked())
            return;

        this.status = DeveloperStatus.BLOCKED;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        if (isDeleted())
            return;

        if (isBlocked())
            throw new DeveloperBlockedException(this.id);

        this.status = DeveloperStatus.DELETED;
        this.updatedAt = Instant.now();
    }

    public void reactivate() {
        if (isActive())
            return;

        if (isBlocked())
            throw new DeveloperBlockedException(this.id);

        this.status = DeveloperStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(DeveloperProfile profile) {
        if (isBlocked())
            throw new DeveloperBlockedException(this.id);

        if (isDeleted())
            throw new DeveloperDeletedException(this.id);

        this.profile = profile;
        this.updatedAt = Instant.now();
    }

    // === Getters ===
    public EntityId getId() {
        return id;
    }

    public DeveloperEmail getEmail() {
        return email;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public DeveloperStatus getStatus() {
        return status;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
