package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.dto.internal.DeveloperDTO;
import com.issuehub.modules.developers.application.exceptions.DeveloperAlreadyExistsException;
import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.application.ports.in.internal.GetDeveloperUseCase;
import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperEmailException;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import com.issuehub.shared.application.dto.DecodedToken;
import com.issuehub.shared.application.exceptions.InvalidTokenException;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(DeveloperController.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProviderPort tokenProviderPort;

    @MockitoBean
    private CreateDeveloperUseCase createDeveloperUseCase;

    @MockitoBean
    private GetDeveloperUseCase getDeveloperUseCase;

    // === create ===
    @Nested
    class Create {

        @Test
        void createDeveloper_shouldReturn201Created() throws Exception {
            var request = new CreateDeveloperRequest("new@example.com");
            var command = new CreateDeveloperCommand(new DeveloperEmail(request.email()));

            mockMvc.perform(post(DeveloperController.DEVELOPERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", DeveloperController.DEVELOPERS + DeveloperController.ME));

            verify(createDeveloperUseCase).execute(command);
        }

        @Test
        void createDeveloper_shouldReturn400BadRequest_whenEmailFormatIsInvalid() throws Exception {
            var request = new CreateDeveloperRequest("not-an-email");

            mockMvc.perform(post(DeveloperController.DEVELOPERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createDeveloper_shouldReturn409Conflict_whenDeveloperAlreadyExists() throws Exception {
            var request = new CreateDeveloperRequest("exists@example.com");

            doThrow(new DeveloperAlreadyExistsException("Developer already exists"))
                    .when(createDeveloperUseCase).execute(any());

            mockMvc.perform(post(DeveloperController.DEVELOPERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        void createDeveloper_shouldReturn400BadRequest_whenDomainValidationFails() throws Exception {
            var request = new CreateDeveloperRequest("invalid@example.com");

            doThrow(new InvalidDeveloperEmailException("Invalid email"))
                    .when(createDeveloperUseCase).execute(any());

            mockMvc.perform(post(DeveloperController.DEVELOPERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

    }

    // === me ===
    @Nested
    class GetMe {

        private final String ME = DeveloperController.DEVELOPERS + DeveloperController.ME;

        private MockHttpServletRequestBuilder authenticated() {
            return get(ME)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        }

        @BeforeEach
        void setup() {
            when(tokenProviderPort.verifyAccessToken("valid-token"))
                    .thenReturn(new DecodedToken(
                            UUID.randomUUID().toString(),
                            "dev@example.com",
                            Map.of(),
                            Instant.now(),
                            Instant.now().plusSeconds(900)
                    ));
        }

        @Test
        void me_shouldReturn200Ok_withDeveloperDTO() throws Exception {
            // Given
            var dto = new DeveloperDTO(
                    UUID.randomUUID(),
                    "dev@example.com",
                    "Dev",
                    "EN",
                    "UTC",
                    Map.of()
            );

            when(getDeveloperUseCase.execute(any())).thenReturn(dto);

            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("dev@example.com"))
                    .andExpect(jsonPath("$.name").value("Dev"))
                    .andExpect(jsonPath("$.language").value("EN"))
                    .andExpect(jsonPath("$.timezone").value("UTC"));
        }

        @Test
        void me_shouldReturn401Unauthorized_whenTokenIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(ME))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void me_shouldReturn401Unauthorized_whenTokenIsInvalid() throws Exception {
            // Given
            when(tokenProviderPort.verifyAccessToken(any()))
                    .thenThrow(new InvalidTokenException("Invalid token"));

            // When/Then
            mockMvc.perform(get(ME)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void me_shouldReturn404NotFound_whenDeveloperNotFound() throws Exception {
            // Given
            when(getDeveloperUseCase.execute(any()))
                    .thenThrow(new DeveloperNotFoundException("Developer not found"));

            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isNotFound());
        }

    }

}
