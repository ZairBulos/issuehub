package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.projections;

import java.util.UUID;

public interface DeveloperProjection {
    UUID getId();
    String getEmail();
    Boolean getIsVerified();
    String getStatus();
}
