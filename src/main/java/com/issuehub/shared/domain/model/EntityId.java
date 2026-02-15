package com.issuehub.shared.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EntityId(UUID value) {

    public EntityId {
        Objects.requireNonNull(value, "EntityId cannot be null");
    }

    public static EntityId generate() {
        return new EntityId(UUID.randomUUID());
    }

}
