package com.issuehub.modules.developers.application.ports.out;

import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;

import java.util.Optional;

public interface DeveloperRepositoryPort {
    Optional<Developer> findByEmail(DeveloperEmail email);
    void save(Developer developer);
}
