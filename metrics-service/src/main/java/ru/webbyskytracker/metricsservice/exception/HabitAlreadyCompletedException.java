package ru.webbyskytracker.metricsservice.exception;

public class HabitAlreadyCompletedException extends RuntimeException{
    public HabitAlreadyCompletedException(String message) {
        super(message);
    }
}
