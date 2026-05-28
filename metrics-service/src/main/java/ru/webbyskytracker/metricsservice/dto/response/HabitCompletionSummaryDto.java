package ru.webbyskytracker.metricsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HabitCompletionSummaryDto {
    private LocalDate completedAt;
    private String note;
}
