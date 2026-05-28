package ru.webbyskytracker.metricsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HabitResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private String color;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer streak;
    private List<LocalDate> completedDates;
}
