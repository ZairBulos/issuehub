package com.issuehub.modules.auth.infrastructure.config;

import com.issuehub.modules.auth.application.ports.in.internal.CreateEmailVerificationUseCase;
import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.modules.auth.application.services.CreateEmailVerificationService;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionOperations;

@Configuration
public class EmailVerificationBeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CreateEmailVerificationUseCase createEmailVerificationUseCase(
            EmailVerificationRepositoryPort repositoryPort,
            EventPublisherPort publisherPort,
            PasswordEncoder hasher,
            TransactionOperations transactionOperations
    ) {
        var service = new CreateEmailVerificationService(repositoryPort, publisherPort, hasher::encode);

        return event -> transactionOperations.executeWithoutResult(status ->
                service.execute(event)
        );
    }

}
