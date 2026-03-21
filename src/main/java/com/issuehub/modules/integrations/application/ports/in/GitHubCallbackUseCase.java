package com.issuehub.modules.integrations.application.ports.in;

import com.issuehub.modules.integrations.application.dto.GitHubCallbackCommand;

public interface GitHubCallbackUseCase {
    void execute(GitHubCallbackCommand command);
}
