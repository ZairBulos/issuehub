package com.issuehub.modules.developers.infrastructure.adapters.in.messaging;

import com.issuehub.modules.developers.application.ports.in.internal.ReactivateDeveloperUseCase;
import com.issuehub.shared.domain.events.DeletedDeveloperLoggedIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedDeveloperLoggedInListener {

    private final ReactivateDeveloperUseCase reactivateDeveloperUseCase;

    @ApplicationModuleListener
    public void on(DeletedDeveloperLoggedIn event) {
        log.info("Received DeletedDeveloperLoggedIn event for developer {}", event.developerId().value());
        reactivateDeveloperUseCase.execute(event.developerId());
    }

}
