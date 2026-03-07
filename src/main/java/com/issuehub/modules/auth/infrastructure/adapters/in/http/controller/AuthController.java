package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.issuehub.modules.auth.application.dto.LoginCommand;
import com.issuehub.modules.auth.application.dto.VerifyEmailCommand;
import com.issuehub.modules.auth.application.ports.in.LoginUseCase;
import com.issuehub.modules.auth.application.ports.in.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.in.VerifyEmailUseCase;
import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.LoginRequest;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.LoginResponse;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.RequestLoginRequest;
import com.issuehub.shared.domain.model.EntityId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(AuthController.AUTH)
@RequiredArgsConstructor
public class AuthController {

    static final String AUTH = "/auth";
    static final String VERIFY_EMAIL = "/verify-email";
    static final String REQUEST_LOGIN = "/request-login";
    static final String LOGIN = "/login";

    private final VerifyEmailUseCase verifyEmailUseCase;
    private final RequestLoginUseCase requestLoginUseCase;
    private final LoginUseCase loginUseCase;

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

    @PostMapping(LOGIN)
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        var ipAddress = Optional.ofNullable(httpRequest.getHeader("X-Forwarded-For"))
                .map(h -> h.split(",")[0].trim())
                .orElseGet(httpRequest::getRemoteAddr);
        if (ipAddress == null || ipAddress.isBlank())
            return ResponseEntity.badRequest().build();

        var userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent == null || userAgent.isBlank())
            return ResponseEntity.badRequest().build();

        log.info("Login attempt for developer: {} from IP: {}", request.email(), ipAddress);

        var result = loginUseCase.execute(new LoginCommand(
                request.email(),
                new LoginCode(request.code()),
                new IpAddress(ipAddress),
                new UserAgent(userAgent)
        ));

        return ResponseEntity.ok().body(LoginResponse.from(result));
    }

}
