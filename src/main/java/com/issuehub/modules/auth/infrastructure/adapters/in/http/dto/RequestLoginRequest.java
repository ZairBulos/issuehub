package com.issuehub.modules.auth.infrastructure.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestLoginRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email

) {
}
