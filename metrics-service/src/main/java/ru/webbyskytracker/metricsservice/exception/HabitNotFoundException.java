package ru.webbyskytracker.metricsservice.exception;

public class HabitNotFoundException extends RuntimeException{
    public HabitNotFoundException(String message) {
        super(message);
    }
}
