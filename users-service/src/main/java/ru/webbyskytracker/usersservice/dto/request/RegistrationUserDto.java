package ru.webbyskytracker.usersservice.dto.request;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String mail;
}
