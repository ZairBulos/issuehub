package com.issuehub.modules.developers.infrastructure.config;

import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.application.services.CreateDeveloperService;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeveloperBeanConfig {

    @Bean
    public CreateDeveloperUseCase createDeveloperUseCase(
            DeveloperRepositoryPort repositoryPort,
            EventPublisherPort publisherPort
    ) {
        return new CreateDeveloperService(repositoryPort, publisherPort);
    }

}
