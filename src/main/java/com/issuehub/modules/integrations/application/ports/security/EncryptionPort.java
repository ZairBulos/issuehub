package com.issuehub.modules.integrations.application.ports.security;

public interface EncryptionPort {
    String encrypt(String value);
    String decrypt(String value);
}
