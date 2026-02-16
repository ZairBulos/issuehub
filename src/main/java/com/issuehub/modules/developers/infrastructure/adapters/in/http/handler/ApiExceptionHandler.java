package com.issuehub.modules.developers.infrastructure.adapters.in.http.handler;

import com.issuehub.modules.developers.application.exceptions.DeveloperAlreadyExistsException;
import com.issuehub.modules.developers.domain.exceptions.DeveloperBlockedException;
import com.issuehub.modules.developers.domain.exceptions.DeveloperDeletedException;
import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperEmailException;
import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperProfileException;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Component("developersApiExceptionHandler")
@RestControllerAdvice(basePackages = "com.issuehub.modules.developers")
public class ApiExceptionHandler {

    // === DOMAIN ==
    @ExceptionHandler({
            InvalidDeveloperEmailException.class,
            InvalidDeveloperProfileException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDeveloperException(Exception ex, HttpServletRequest request) {
        log.warn("Invalid developer at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DeveloperBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDeveloperBlockedException(DeveloperBlockedException ex, HttpServletRequest request) {
        log.warn("Developer blocked at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DeveloperDeletedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDeveloperDeletedException(DeveloperDeletedException ex, HttpServletRequest request) {
        log.warn("Developer deleted at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
    }

    // === APPLICATION ===
    @ExceptionHandler(DeveloperAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDeveloperAlreadyExistsException(DeveloperAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Developer already exists at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
    }

}
