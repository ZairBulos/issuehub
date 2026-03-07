package com.issuehub.modules.developers.application.dto;

import com.issuehub.shared.domain.model.EntityId;

public record DeveloperView(
        EntityId id,
        String email,
        Boolean isVerified,
        String status
) {

    public boolean isActive() {
        return status.equalsIgnoreCase("active");
    }

    public boolean isBlocked() {
        return status.equalsIgnoreCase("blocked");
    }

    public boolean isDeleted() {
        return status.equalsIgnoreCase("deleted");
    }

}
