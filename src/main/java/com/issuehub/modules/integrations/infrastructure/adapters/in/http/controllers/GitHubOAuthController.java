package com.issuehub.modules.integrations.infrastructure.adapters.in.http.controllers;

import com.issuehub.modules.integrations.application.dto.GitHubCallbackCommand;
import com.issuehub.modules.integrations.application.ports.in.GitHubCallbackUseCase;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping(GitHubOAuthController.INTEGRATIONS_GITHUB)
@RequiredArgsConstructor
public class GitHubOAuthController {

    static final String INTEGRATIONS_GITHUB = "/integrations/github";
    static final String CONNECT = "/connect";
    static final String CALLBACK = "/callback";

    private final GitHubProperties gitHubProperties;
    private final GitHubCallbackUseCase gitHubCallbackUseCase;

    private final Map<String, String> store = new ConcurrentHashMap<>();

    @GetMapping(CONNECT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> connect(@AuthenticationPrincipal final String email) {
        log.info("GitHub OAuth connect for developer: {}", email);

        final var state = UUID.randomUUID().toString();
        store.put(state, email);

        var url = "https://github.com/login/oauth/authorize"
                + "?client_id=" + gitHubProperties.clientId()
                + "&redirect_uri=" + gitHubProperties.callbackUrl()
                + "&state=" + state;

        return ResponseEntity.ok().location(URI.create(url)).build();
    }

    @GetMapping(CALLBACK)
    public ResponseEntity<Void> callback(
            @RequestParam final String code,
            @RequestParam final String state
    ) {
        final var email = store.get(state);
        store.remove(state);

        log.info("GitHub OAuth callback for developer: {}", email);

        gitHubCallbackUseCase.execute(new GitHubCallbackCommand(code, email));

        return ResponseEntity.ok().build();
    }

}
