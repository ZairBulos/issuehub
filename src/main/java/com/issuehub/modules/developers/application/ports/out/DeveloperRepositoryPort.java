package com.issuehub.modules.developers.application.ports.out;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public interface DeveloperRepositoryPort {
    Optional<Developer> findByEmail(DeveloperEmail email);
    Optional<Developer> findById(EntityId id);
    Optional<DeveloperView> findViewById(EntityId id);
    void save(Developer developer);
}
