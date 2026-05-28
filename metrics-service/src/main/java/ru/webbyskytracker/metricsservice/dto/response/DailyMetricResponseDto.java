package ru.webbyskytracker.metricsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DailyMetricResponseDto {

    private Long id;
    private Long userId;
    private LocalDate date;
    private Float sleepHours;
    private Integer mood;
    private Integer productivity;
    private Integer energy;
    private Integer waterGlasses;
    private Integer exerciseMinutes;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
