package ru.webbyskytracker.usersservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDtoResponse {
    private String username;
    private String mail;
    protected String message;
}
