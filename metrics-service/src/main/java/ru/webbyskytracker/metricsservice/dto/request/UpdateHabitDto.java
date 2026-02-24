package ru.webbyskytracker.metricsservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateHabitDto {
    @NotNull
    @Size(min = 3, max = 100)
    private String name;
    @NotNull
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color;
    @NotNull
    private Boolean isActive;

}
