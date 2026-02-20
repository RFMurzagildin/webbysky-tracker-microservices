package ru.webbyskytracker.metricsservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.webbyskytracker.metricsservice.dto.response.ErrorResponse;
import ru.webbyskytracker.metricsservice.exception.InvalidTokenException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlerInvalidTokenExceptions(
            InvalidTokenException ex,
            HttpServletRequest request
    ){
        log.warn("Invalid token: {}", ex.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getServletPath());
    }
}
