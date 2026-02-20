package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.exceptions.DeveloperAlreadyExistsException;
import com.issuehub.modules.developers.application.ports.in.internal.CreateDeveloperUseCase;
import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperEmailException;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(DeveloperController.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateDeveloperUseCase createDeveloperUseCase;

    // === create ===
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
