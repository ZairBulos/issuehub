package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByIdUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public class FindDeveloperService implements FindDeveloperByIdUseCase, FindDeveloperByEmailUseCase {

    private final DeveloperRepositoryPort repositoryPort;

    public FindDeveloperService(DeveloperRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Optional<DeveloperView> execute(EntityId developerId) {
        return repositoryPort.findViewById(developerId);
    }

    @Override
    public Optional<DeveloperView> execute(String developerEmail) {
        return repositoryPort.findViewByEmail(new DeveloperEmail(developerEmail));
    }

}
