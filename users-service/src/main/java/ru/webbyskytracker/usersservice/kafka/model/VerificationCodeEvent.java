package ru.webbyskytracker.usersservice.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeEvent {
    private String email;
    private String code;
}
