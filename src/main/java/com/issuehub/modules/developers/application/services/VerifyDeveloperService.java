package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.in.internal.VerifyDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.shared.domain.model.EntityId;

public class VerifyDeveloperService implements VerifyDeveloperUseCase {

    private final DeveloperRepositoryPort repositoryPort;

    public VerifyDeveloperService(DeveloperRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void execute(EntityId developerId) {
        var developer = repositoryPort.findById(developerId)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));

        developer.verify();
        repositoryPort.save(developer);
    }

}
