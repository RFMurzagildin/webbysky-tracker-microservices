package ru.webbyskytracker.usersservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.dto.request.VerifyEmailDto;
import ru.webbyskytracker.usersservice.dto.response.UserDtoResponse;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDtoResponse registerNewUser(@Valid @RequestBody RegistrationUserDto dto){
            User user = userService.initiateRegistration(dto);
            return new UserDtoResponse(
                    user.getUsername(),
                    user.getMail(),
                    "Verification code sent to " + user.getMail()
            );
    }

    @PostMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    public UserDtoResponse verifyMail(@Valid @RequestBody VerifyEmailDto verifyEmailDto){
        User user = userService.verifyMail(verifyEmailDto);
        return new UserDtoResponse(
                user.getUsername(),
                user.getMail(),
                "Mail has been successfully confirmed"
        );
    }
}
