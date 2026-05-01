package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.ports.in.internal.DeveloperDetails;
import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.in.internal.GetDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;

public class GetDeveloperService implements GetDeveloperUseCase {

    private final DeveloperRepositoryPort repositoryPort;

    public GetDeveloperService(DeveloperRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public DeveloperDetails execute(DeveloperEmail developerEmail) {
        return repositoryPort.findByEmail(developerEmail)
                .map(DeveloperDetails::from)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
    }

}
