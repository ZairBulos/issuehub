package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;
import com.issuehub.modules.auth.application.ports.in.internal.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.in.internal.VerifyEmailUseCase;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.RequestLoginRequest;
import com.issuehub.shared.domain.model.EntityId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(AuthController.AUTH)
@RequiredArgsConstructor
public class AuthController {

    static final String AUTH = "/auth";
    static final String VERIFY_EMAIL = "/verify-email";
    static final String REQUEST_LOGIN = "/request-login";

    private final VerifyEmailUseCase verifyEmailUseCase;
    private final RequestLoginUseCase requestLoginUseCase;

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<Void> verifyEmail(
            @RequestParam UUID developerId,
            @RequestParam String code
    ) {
        log.info("Verifying developer: {}", developerId);

        verifyEmailUseCase.execute(new VerifyEmailCommand(
                new EntityId(developerId),
                new VerificationCode(code)
        ));

        return ResponseEntity.noContent().build();
    }

    @PostMapping(REQUEST_LOGIN)
    public ResponseEntity<Void> requestLogin(@Valid @RequestBody RequestLoginRequest request) {
        log.info("Requesting login for developer: {}", request.email());

        requestLoginUseCase.execute(request.email());

        return ResponseEntity.ok().build();
    }

}
