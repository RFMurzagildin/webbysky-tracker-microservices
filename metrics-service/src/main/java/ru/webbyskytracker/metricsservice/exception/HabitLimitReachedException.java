package ru.webbyskytracker.metricsservice.exception;

public class HabitLimitReachedException extends RuntimeException {
    public HabitLimitReachedException(String message) {
        super(message);
    }
}
