package com.issuehub.modules.notifications.infrastructure.adapters.in.messaging;

import com.issuehub.modules.auth.domain.events.LoginVerificationCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginVerificationCreatedListener {

    @ApplicationModuleListener
    public void on(LoginVerificationCreated event) {
        log.info(
                "Received LoginVerificationCreated event for developer {} with code {}",
                event.developerId(),
                event.verificationCode()
        );
    }

}
