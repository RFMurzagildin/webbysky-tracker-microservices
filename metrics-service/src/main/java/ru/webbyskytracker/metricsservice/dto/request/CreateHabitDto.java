package ru.webbyskytracker.metricsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateHabitDto {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color = "#4CAF50";
}
