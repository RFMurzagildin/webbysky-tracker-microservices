package ru.webbyskytracker.metricsservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.webbyskytracker.metricsservice.dto.response.ErrorResponse;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyExistsException;
import ru.webbyskytracker.metricsservice.exception.HabitNotFoundException;
import ru.webbyskytracker.metricsservice.exception.InvalidTokenException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlerInvalidTokenExceptions(
        InvalidTokenException ex,
        HttpServletRequest request
    ){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(HabitAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerHabitAlreadyExistsException(
        HabitAlreadyExistsException ex,
        HttpServletRequest request
    ){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getServletPath()
        );
    }

    @ExceptionHandler(HabitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerHabitNotFoundException(
            HabitNotFoundException ex,
            HttpServletRequest request
    ){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getServletPath()
        );
    }
}
