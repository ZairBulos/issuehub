package com.issuehub.shared.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.warn("Unauthorized access at [{}]: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var errorResponse = ErrorResponse.of(
                "UNAUTHORIZED",
                "Authentication is required to access this resource",
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getRequestURI()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
