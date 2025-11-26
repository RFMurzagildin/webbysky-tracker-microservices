package ru.webbyskytracker.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody RegistrationUserDto registrationUserDto){
        return userService.save(registrationUserDto);
    }


}
