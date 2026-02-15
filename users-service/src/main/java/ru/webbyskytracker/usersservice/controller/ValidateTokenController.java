package ru.webbyskytracker.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.webbyskytracker.usersservice.service.UserService;

@RestController
@RequiredArgsConstructor
public class ValidateTokenController {

    private final UserService userService;

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        return userService.validateToken(authorizationHeader);
    }
}
