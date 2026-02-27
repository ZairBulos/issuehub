package com.issuehub.modules.notifications.infrastructure.adapters.in.messaging;

import com.issuehub.modules.auth.domain.events.EmailVerificationCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailVerificationCreatedListener {

    @ApplicationModuleListener
    public void on(EmailVerificationCreated event) {
        log.info(
                "Received EmailVerificationCreated event for developer {} with code {}",
                event.developerId().value(),
                event.verificationCode()
        );
    }

}
