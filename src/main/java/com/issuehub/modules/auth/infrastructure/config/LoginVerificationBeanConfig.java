package com.issuehub.modules.auth.infrastructure.config;

import com.issuehub.modules.auth.application.ports.in.LoginUseCase;
import com.issuehub.modules.auth.application.ports.in.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.out.AuthSessionRepositoryPort;
import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.application.services.LoginService;
import com.issuehub.modules.auth.application.services.RequestLoginService;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionOperations;

@Configuration
public class LoginVerificationBeanConfig {

    @Bean
    public RequestLoginUseCase requestLoginUseCase(
            LoginVerificationRepositoryPort repositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            EventPublisherPort publisherPort,
            PasswordEncoder hasher,
            TransactionOperations transactionOperations
    ) {
        var service = new RequestLoginService(repositoryPort, findDeveloperByEmailUseCase, publisherPort, hasher::encode);

        return command -> transactionOperations.executeWithoutResult(status ->
                service.execute(command)
        );
    }

    @Bean
    public LoginUseCase loginUseCase(
            LoginVerificationRepositoryPort repositoryPort,
            AuthSessionRepositoryPort authSessionRepositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            TokenProviderPort tokenProviderPort,
            EventPublisherPort publisherPort,
            PasswordEncoder verifier,
            TransactionOperations transactionOperations
    ) {
        var service = new LoginService(
                repositoryPort,
                authSessionRepositoryPort,
                findDeveloperByEmailUseCase,
                tokenProviderPort,
                publisherPort,
                verifier::matches
        );

        return command -> transactionOperations.execute(status ->
                service.execute(command)
        );
    }

}
