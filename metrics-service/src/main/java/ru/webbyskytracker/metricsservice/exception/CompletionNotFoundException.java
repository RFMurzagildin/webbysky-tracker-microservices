package ru.webbyskytracker.metricsservice.exception;

public class CompletionNotFoundException extends RuntimeException {
    public CompletionNotFoundException(String message) {
        super(message);
    }
}
