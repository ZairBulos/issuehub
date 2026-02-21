package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.projections;

import java.util.UUID;

public interface DeveloperProjection {
    UUID id();
    String email();
    Boolean isVerified();
    String status();
}
