package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.dto.internal.DeveloperDTO;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.GetDeveloperUseCase;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(DeveloperController.DEVELOPERS)
@RequiredArgsConstructor
public class DeveloperController {

    static final String DEVELOPERS = "/developers";
    static final String ME = "/me";

    private final CreateDeveloperUseCase createDeveloperUseCase;
    private final GetDeveloperUseCase getDeveloperUseCase;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateDeveloperRequest request) {
        log.info("Creating developer: {}", request.email());

        createDeveloperUseCase.execute(new CreateDeveloperCommand(new DeveloperEmail(request.email())));

        return ResponseEntity.created(URI.create(DEVELOPERS + ME)).build();
    }

    @GetMapping(ME)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeveloperDTO> me(@AuthenticationPrincipal String email) {
        log.info("Getting developer: {}", email);

        var developer = getDeveloperUseCase.execute(new DeveloperEmail(email));

        return ResponseEntity.ok(developer);
    }

}
