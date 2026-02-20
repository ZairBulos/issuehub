package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.exceptions.DeveloperAlreadyExistsException;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.shared.domain.events.DeveloperCreated;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.shared.application.ports.out.EventPublisherPort;

import java.time.Instant;

public class CreateDeveloperService implements CreateDeveloperUseCase {

    private final DeveloperRepositoryPort repositoryPort;
    private final EventPublisherPort publisherPort;

    public CreateDeveloperService(DeveloperRepositoryPort repositoryPort, EventPublisherPort publisherPort) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
    }

    @Override
    public void execute(CreateDeveloperCommand command) {
        // RN: Developer must not exist
        repositoryPort.findByEmail(command.email())
                .ifPresent(developer -> {
                    throw new DeveloperAlreadyExistsException("Developer already exists");
                });

        var developer = Developer.create(command.email(), DeveloperProfile.defaultProfile());
        repositoryPort.save(developer);

        publisherPort.publish(new DeveloperCreated(
                developer.getId(),
                developer.getEmail().value(),
                Instant.now()
        ));
    }

}
