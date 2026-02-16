package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(DeveloperController.DEVELOPERS)
@RequiredArgsConstructor
public class DeveloperController {

    static final String DEVELOPERS = "/developer";
    static final String ME = "/me";

    private final CreateDeveloperUseCase createDeveloperUseCase;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateDeveloperRequest request) {
        log.info("Creating developer: {}", request.email());

        createDeveloperUseCase.execute(new CreateDeveloperCommand(new DeveloperEmail(request.email())));

        return ResponseEntity.created(URI.create(DEVELOPERS + ME)).build();
    }

}
