package com.issuehub.modules.auth.application.ports.in;

public interface RequestLoginUseCase {
    void execute(String email);
}
