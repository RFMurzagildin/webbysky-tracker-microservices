package ru.webbyskytracker.usersservice.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.webbyskytracker.usersservice.dto.response.ErrorResponse;
import ru.webbyskytracker.usersservice.exception.*;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            PasswordMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handlerUserRegistrationErrors(RuntimeException e){
        log.info("Registration failed: {}", e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Registration failed: " + e.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.info("Validation failed: {}", message);
        return new ResponseEntity<>(
                new ErrorResponse("Validation failed: " + message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCode(InvalidVerificationCodeException e){
        log.info(e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e){
        log.info(e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }


}
