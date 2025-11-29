package ru.webbyskytracker.usersservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.webbyskytracker.usersservice.validation.annotation.Password;
import ru.webbyskytracker.usersservice.validation.annotation.Username;

@Data
public class RegistrationUserDto {
    @NotBlank
    @Size(min = 4, max = 12)
    @Username
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    private String confirmPassword;

}
