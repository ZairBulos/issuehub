package com.issuehub.shared.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        log.warn("Forbidden access at [{}]: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var errorResponse = ErrorResponse.of(
                "FORBIDDEN",
                "You do not have permission to access this resource",
                HttpServletResponse.SC_FORBIDDEN,
                request.getRequestURI()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
