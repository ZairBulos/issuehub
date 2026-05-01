package com.issuehub.modules.integrations.application.ports.in;

public interface GitHubCallbackUseCase {
    void execute(GitHubCallbackCommand command);
}
