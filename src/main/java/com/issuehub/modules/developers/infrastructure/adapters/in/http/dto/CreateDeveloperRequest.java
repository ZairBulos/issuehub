package com.issuehub.modules.developers.infrastructure.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateDeveloperRequest(

        @NotBlank(message = "Developer email cannot be blank")
        @Email(message = "Developer email must be valid")
        String email

) {
}
