package com.issuehub.modules.auth.application.ports.in.internal;

public interface RequestLoginUseCase {
    void execute(String email);
}
