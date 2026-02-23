package ru.webbyskytracker.metricsservice.exception;

public class HabitAlreadyExistsException extends RuntimeException{
    public HabitAlreadyExistsException(String message) {
        super(message);
    }
}
