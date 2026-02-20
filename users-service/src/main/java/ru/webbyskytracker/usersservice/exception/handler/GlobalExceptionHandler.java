package ru.webbyskytracker.usersservice.exception.handler;

import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.webbyskytracker.usersservice.dto.response.ErrorResponse;
import ru.webbyskytracker.usersservice.exception.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            PasswordMismatchException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlerUserRegistrationErrors(
            RuntimeException ex,
            HttpServletRequest request
    ){
        log.info("Registration failed: {}", ex.getMessage());
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Registration failed: " + ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.info("Validation failed: {}", message);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed: " + message,
                request.getServletPath()
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCode(
            InvalidVerificationCodeException ex,
            HttpServletRequest request
    ){
        log.info(ex.getMessage());
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ){
        log.info(ex.getMessage());
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(
            HttpStatus.FORBIDDEN
    )
    public ErrorResponse handleException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Authentication Failed" + ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidRefresh(
            InvalidRefreshTokenException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUserNotVerified(
            UserNotVerifiedException e,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN,
                e.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(RedisConnectionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRedisNotConnection(
            RedisConnectionException e,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request.getServletPath()
        );
    }
}