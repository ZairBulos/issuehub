package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.modules.auth.application.dto.AuthTokensResult;
import com.issuehub.modules.auth.application.dto.VerifyEmailCommand;
import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.AccountNotVerifiedException;
import com.issuehub.modules.auth.application.exceptions.ActiveLoginCodeNotFoundException;
import com.issuehub.modules.auth.application.ports.in.LoginUseCase;
import com.issuehub.modules.auth.application.ports.in.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.in.VerifyEmailUseCase;
import com.issuehub.modules.auth.domain.exceptions.*;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.LoginRequest;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.RequestLoginRequest;
import com.issuehub.shared.domain.model.EntityId;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @MockitoBean
    private LoginUseCase loginUseCase;

    // === verify email ===
    @Nested
    class VerifyEmail {

        private final String VERIFY_EMAIL = AuthController.AUTH + AuthController.VERIFY_EMAIL;

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

    // === request login ===
    @Nested
    class RequestLogin {

        private final String REQUEST_LOGIN = AuthController.AUTH + AuthController.REQUEST_LOGIN;

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
        void requestLogin_shouldReturn401Unauthorized_whenAccountNotFound() throws Exception {
            // Given
            var email = "missing@example.com";
            var request = new RequestLoginRequest(email);

            doThrow(new AccountNotFoundException("Invalid credentials"))
                    .when(requestLoginUseCase).execute(email);

            // When/Then
            mockMvc.perform(post(REQUEST_LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
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

    // === login ===
    @Nested
    class Login {

        private final String LOGIN = AuthController.AUTH + AuthController.LOGIN;

        private LoginRequest loginRequest() {
            return new LoginRequest("login@example.com", "123456");
        }

        private MockHttpServletRequestBuilder loginPost(LoginRequest request) throws Exception {
            return post(LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Forwarded-For", "10.0.0.1")
                    .header("User-Agent", "Mozilla/5.0")
                    .content(objectMapper.writeValueAsString(request));
        }

        @Test
        void login_shouldReturn200Ok_whenLoginSucceeds() throws Exception {
            // Given
            var request = loginRequest();
            var result = new AuthTokensResult("access-token", "refresh-token");

            when(loginUseCase.execute(any())).thenReturn(result);

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
        }

        @Test
        void login_shouldReturn401Unauthorized_whenAccountNotFound() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new AccountNotFoundException("Invalid credentials"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void login_shouldReturn403Forbidden_whenAccountBlocked() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new AccountBlockedException("Account is blocked"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isForbidden());
        }

        @Test
        void login_shouldReturn404NotFound_whenActiveCodeNotFound() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new ActiveLoginCodeNotFoundException("No active code found"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isNotFound());
        }

        @Test
        void login_shouldReturn409Conflict_whenCodeAlreadyUsed() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new LoginCodeAlreadyUsedException("Login code already used"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isConflict());
        }

        @Test
        void login_shouldReturn410Gone_whenCodeExpired() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new LoginCodeExpiredException("Login code expired"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isGone());
        }

        @Test
        void login_shouldReturn401BadRequest_whenCodeIsInvalid() throws Exception {
            // Given
            var request = loginRequest();

            when(loginUseCase.execute(any()))
                    .thenThrow(new InvalidLoginCodeException("Invalid login code"));

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void login_shouldReturn400BadRequest_whenEmailIsBlank() throws Exception {
            // Given
            var request = new LoginRequest("", "123456");

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void login_shouldReturn400BadRequest_whenEmailIsInvalid() throws Exception {
            // Given
            var request = new LoginRequest("invalid-email", "123456");

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void login_shouldReturn400BadRequest_whenCodeIsBlank() throws Exception {
            // Given
            var request = new LoginRequest("dev@example.com", "");

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void login_shouldReturn400BadRequest_whenCodeIsNotSixDigits() throws Exception {
            // Given
            var request = new LoginRequest("dev@example.com", "123");

            // When/Then
            mockMvc.perform(loginPost(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400BadRequest_whenUserAgentIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(post(LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Forwarded-For", "10.0.0.1")
                            .content(objectMapper.writeValueAsString(loginRequest())))
                    .andExpect(status().isBadRequest());
        }

    }

}
