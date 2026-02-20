package ru.webbyskytracker.metricsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HabitResponseDto {
    private Long id;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}
