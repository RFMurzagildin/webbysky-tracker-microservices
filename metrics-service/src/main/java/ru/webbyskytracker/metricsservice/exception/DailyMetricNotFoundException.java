package ru.webbyskytracker.metricsservice.exception;

public class DailyMetricNotFoundException extends RuntimeException {
    public DailyMetricNotFoundException(String message) {
        super(message);
    }
}
