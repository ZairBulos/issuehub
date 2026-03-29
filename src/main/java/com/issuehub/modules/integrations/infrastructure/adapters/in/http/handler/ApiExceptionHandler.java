package com.issuehub.modules.integrations.infrastructure.adapters.in.http.handler;

import com.issuehub.modules.integrations.application.exceptions.AccountBlockedException;
import com.issuehub.modules.integrations.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.exceptions.OAuthConnectionNotFoundException;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Component("integrationsApiExceptionHandler")
@RestControllerAdvice(basePackages = "com.issuehub.modules.integrations")
public class ApiExceptionHandler {

    // === APPLICATION ===
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException ex, HttpServletRequest request) {
        log.warn("Account not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccountBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccountBlockedException(AccountBlockedException ex, HttpServletRequest request) {
        log.warn("Account blocked at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(GitHubApiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleGitHubApiException(GitHubApiException ex, HttpServletRequest request) {
        log.error("GitHub API error at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_GATEWAY.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(OAuthConnectionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOAuthConnectionNotFoundException(OAuthConnectionNotFoundException ex, HttpServletRequest request) {
        log.error("Connection not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
    }

}
