package ru.webbyskytracker.aiservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.webbyskytracker.aiservice.dto.response.ErrorResponse;
import ru.webbyskytracker.aiservice.exception.LlmUnavailableException;
import ru.webbyskytracker.aiservice.exception.RateLimitException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleRateLimit(RateLimitException ex, HttpServletRequest req) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage(), req.getServletPath());
    }

    @ExceptionHandler(LlmUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleLlmUnavailable(LlmUnavailableException ex, HttpServletRequest req) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(), req.getServletPath());
    }
}
