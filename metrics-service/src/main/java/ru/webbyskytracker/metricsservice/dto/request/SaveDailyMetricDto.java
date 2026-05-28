package ru.webbyskytracker.metricsservice.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SaveDailyMetricDto {

    private LocalDate date;

    private Float sleepHours;

    private Integer mood;

    private Integer productivity;

    private Integer energy;

    private Integer waterGlasses;

    private Integer exerciseMinutes;

    private String note;
}
