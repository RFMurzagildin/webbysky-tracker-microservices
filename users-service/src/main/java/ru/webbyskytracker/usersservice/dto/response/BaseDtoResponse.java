package ru.webbyskytracker.usersservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseDtoResponse {
    private Long userId;
    private String username;
    private String email;
    protected String message;
}
