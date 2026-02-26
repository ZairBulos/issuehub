package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;
import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.AccountNotVerifiedException;
import com.issuehub.modules.auth.application.ports.in.internal.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.in.internal.VerifyEmailUseCase;
import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.RequestLoginRequest;
import com.issuehub.shared.domain.model.EntityId;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VerifyEmailUseCase verifyEmailUseCase;

    @MockitoBean
    private RequestLoginUseCase requestLoginUseCase;

    private final String VERIFY_EMAIL = AuthController.AUTH + AuthController.VERIFY_EMAIL;
    private final String REQUEST_LOGIN = AuthController.AUTH + AuthController.REQUEST_LOGIN;

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

    // === request login ===
    @Test
    void requestLogin_shouldReturn200Ok_whenLoginRequestSucceeds() throws Exception {
        // Given
        var email = "request_login@email.com";
        var request = new RequestLoginRequest(email);

        doNothing().when(requestLoginUseCase).execute(email);

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(requestLoginUseCase).execute(email);
    }

    @Test
    void requestLogin_shouldReturn404NotFound_whenAccountNotFound() throws Exception {
        // Given
        var email = "missing@example.com";
        var request = new RequestLoginRequest(email);

        doThrow(new AccountNotFoundException("Account not found " + email))
                .when(requestLoginUseCase).execute(email);

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestLogin_shouldReturn403Forbidden_whenAccountBlocked() throws Exception {
        // Given
        var email = "blocked@example.com";
        var request = new RequestLoginRequest(email);

        doThrow(new AccountBlockedException("Account is blocked " + email))
                .when(requestLoginUseCase).execute(email);

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestLogin_shouldReturn403Forbidden_whenAccountNotVerified() throws Exception {
        // Given
        var email = "not_verified@example.com";
        var request = new RequestLoginRequest(email);

        doThrow(new AccountNotVerifiedException("Account is not verified " + email))
                .when(requestLoginUseCase).execute(email);

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestLogin_shouldReturn400BadRequest_whenEmailIsBlank() throws Exception {
        // Given
        var request = new RequestLoginRequest("");

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestLogin_shouldReturn400BadRequest_whenEmailIsInvalid() throws Exception {
        // Given
        var request = new RequestLoginRequest("invalid-email");

        // When/Then
        mockMvc.perform(post(REQUEST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
