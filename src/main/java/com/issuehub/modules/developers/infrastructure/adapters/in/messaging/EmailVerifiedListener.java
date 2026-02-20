package com.issuehub.modules.developers.infrastructure.adapters.in.messaging;

import com.issuehub.modules.developers.application.ports.in.internal.VerifyDeveloperUseCase;
import com.issuehub.shared.domain.events.EmailVerified;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerifiedListener {

    private final VerifyDeveloperUseCase verifyDeveloperUseCase;

    @ApplicationModuleListener
    public void on(EmailVerified event) {
        log.info("Received EmailVerified event for developer {}", event.developerId());
        verifyDeveloperUseCase.execute(event.developerId());
    }

}
