package ru.webbyskytracker.metricsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CompletionResponseDto {
    private Long id;
    private Long habitId;
    private String habitName;
    private LocalDate completedAt;
    private String note;
    private LocalDateTime createdAt;
}
