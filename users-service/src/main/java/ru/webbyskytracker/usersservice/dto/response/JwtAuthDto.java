package ru.webbyskytracker.usersservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthDto {
    private String accessToken;
    private String refreshToken;
}
