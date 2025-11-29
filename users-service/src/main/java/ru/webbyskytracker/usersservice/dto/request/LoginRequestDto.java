package ru.webbyskytracker.usersservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.webbyskytracker.usersservice.validation.annotation.Password;

@Data
public class LoginRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Password
    private String password;
}
