package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.in.internal.ReactivateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.shared.domain.model.EntityId;

public class ReactivateDeveloperService implements ReactivateDeveloperUseCase {

    private final DeveloperRepositoryPort repositoryPort;

    public ReactivateDeveloperService(DeveloperRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void execute(EntityId developerId) {
        var developer = repositoryPort.findById(developerId)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));

        developer.reactivate();
        repositoryPort.save(developer);
    }

}
