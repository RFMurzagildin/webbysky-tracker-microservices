package ru.webbyskytracker.metricsservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCompletionDto {
    @NotNull
    private Long habitId;
    @NotNull
    private LocalDate completedAt;
    @Size(max = 500)
    @NotNull
    private String note;
}
