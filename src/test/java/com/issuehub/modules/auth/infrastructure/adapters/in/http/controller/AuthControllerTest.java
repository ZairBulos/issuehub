package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;
import com.issuehub.modules.auth.application.ports.in.internal.VerifyEmailUseCase;
import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.shared.domain.model.EntityId;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VerifyEmailUseCase verifyEmailUseCase;

    private final String VERIFY_EMAIL = AuthController.AUTH + AuthController.VERIFY_EMAIL;

    // === verify email ===
    @Test
    void verifyEmail_shouldReturn204NoContent_whenVerificationSucceeds() throws Exception {
        // Given
        var command = new VerifyEmailCommand(
                EntityId.generate(),
                VerificationCode.generate()
        );

        doNothing().when(verifyEmailUseCase).execute(command);

        // When/Then
        mockMvc.perform(get(VERIFY_EMAIL)
                        .param("developerId", command.developerId().value().toString())
                        .param("code", command.code().value()))
                .andExpect(status().isNoContent());

        verify(verifyEmailUseCase).execute(command);
    }

    @Test
    void verifyEmail_shouldReturn409Conflict_whenCodeAlreadyUsed() throws Exception {
        // Given
        var command = new VerifyEmailCommand(
                EntityId.generate(),
                VerificationCode.generate()
        );

        doThrow(new VerificationCodeAlreadyUsedException("Verification code has already been used"))
                .when(verifyEmailUseCase).execute(command);

        // When/Then
        mockMvc.perform(get(VERIFY_EMAIL)
                        .param("developerId", command.developerId().value().toString())
                        .param("code", command.code().value()))
                .andExpect(status().isConflict());
    }

    @Test
    void verifyEmail_shouldReturn410Gone_whenCodeExpired() throws Exception {
        // Given
        var command = new VerifyEmailCommand(
                EntityId.generate(),
                VerificationCode.generate()
        );

        doThrow(new VerificationCodeExpiredException("Verification code has expired"))
                .when(verifyEmailUseCase).execute(command);

        // When/Then
        mockMvc.perform(get(VERIFY_EMAIL)
                        .param("developerId", command.developerId().value().toString())
                        .param("code", command.code().value()))
                .andExpect(status().isGone());
    }

    @Test
    void verifyEmail_shouldReturn400BadRequest_whenCodeIsInvalid() throws Exception {
        // Given
        var command = new VerifyEmailCommand(
                EntityId.generate(),
                VerificationCode.generate()
        );

        doThrow(new InvalidVerificationCodeException("Invalid verification code"))
                .when(verifyEmailUseCase).execute(command);

        // When/Then
        mockMvc.perform(get(VERIFY_EMAIL)
                        .param("developerId", command.developerId().value().toString())
                        .param("code", command.code().value()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_shouldReturn400BadRequest_whenVerificationNotFound() throws Exception {
        // Given
        var command = new VerifyEmailCommand(
                EntityId.generate(),
                VerificationCode.generate()
        );

        doThrow(new InvalidVerificationCodeException("Not found verification code"))
                .when(verifyEmailUseCase).execute(command);

        // When/Then
        mockMvc.perform(get(VERIFY_EMAIL)
                        .param("developerId", command.developerId().value().toString())
                        .param("code", command.code().value()))
                .andExpect(status().isBadRequest());
    }

}
