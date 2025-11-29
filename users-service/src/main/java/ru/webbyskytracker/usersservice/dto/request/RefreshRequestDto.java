package ru.webbyskytracker.usersservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDto {
    @NotBlank
    private String refreshToken;
}
