package ru.webbyskytracker.usersservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "Code must be 5 digits")
    private String code;
}
