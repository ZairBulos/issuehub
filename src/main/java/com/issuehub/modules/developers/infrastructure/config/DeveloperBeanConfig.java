package com.issuehub.modules.developers.infrastructure.config;

import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByIdUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.GetDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.ReactivateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.VerifyDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.application.services.*;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionOperations;

@Configuration
public class DeveloperBeanConfig {

    @Bean
    public CreateDeveloperUseCase createDeveloperUseCase(
            DeveloperRepositoryPort repositoryPort,
            EventPublisherPort publisherPort,
            TransactionOperations transactionOperations
    ) {
        var service = new CreateDeveloperService(repositoryPort, publisherPort);

        return command -> transactionOperations.executeWithoutResult(status ->
                service.execute(command)
        );
    }

    @Bean
    public VerifyDeveloperUseCase verifyDeveloperUseCase(
            DeveloperRepositoryPort repositoryPort,
            TransactionOperations transactionOperations
    ) {
        var service = new VerifyDeveloperService(repositoryPort);

        return command -> transactionOperations.executeWithoutResult(status ->
                service.execute(command)
        );
    }

    @Bean
    public ReactivateDeveloperUseCase reactivateDeveloperUseCase(
            DeveloperRepositoryPort repositoryPort,
            TransactionOperations transactionOperations
    ) {
        var service = new ReactivateDeveloperService(repositoryPort);

        return command -> transactionOperations.executeWithoutResult(status ->
                service.execute(command)
        );
    }

    @Bean
    public GetDeveloperUseCase getDeveloperUseCase(DeveloperRepositoryPort repositoryPort) {
        return new GetDeveloperService(repositoryPort);
    }

    @Bean
    public FindDeveloperByIdUseCase findDeveloperByIdUseCase(DeveloperRepositoryPort repositoryPort) {
        return new FindDeveloperService(repositoryPort);
    }

    @Bean
    public FindDeveloperByEmailUseCase findDeveloperByEmailUseCase(DeveloperRepositoryPort repositoryPort) {
        return new FindDeveloperService(repositoryPort);
    }

}
