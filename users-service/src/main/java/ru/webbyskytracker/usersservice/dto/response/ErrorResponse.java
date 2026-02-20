package ru.webbyskytracker.usersservice.dto.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();
    private HttpStatus status;
    private String message;
    private String path;

    public ErrorResponse(HttpStatus status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }
}
