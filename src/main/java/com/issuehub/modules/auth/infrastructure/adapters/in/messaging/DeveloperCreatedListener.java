package com.issuehub.modules.auth.infrastructure.adapters.in.messaging;

import com.issuehub.modules.auth.application.ports.in.internal.CreateEmailVerificationUseCase;
import com.issuehub.modules.developers.domain.events.DeveloperCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeveloperCreatedListener {

    private final CreateEmailVerificationUseCase createEmailVerificationUseCase;

    @ApplicationModuleListener
    public void on(DeveloperCreated event) {
        log.info("Received DeveloperCreated event for developer {}", event.developerId());
        createEmailVerificationUseCase.execute(event);
    }

}
