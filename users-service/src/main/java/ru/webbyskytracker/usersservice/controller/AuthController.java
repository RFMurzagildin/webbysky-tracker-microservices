package ru.webbyskytracker.usersservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.usersservice.dto.request.LoginRequestDto;
import ru.webbyskytracker.usersservice.dto.request.RefreshRequestDto;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.dto.request.VerifyEmailDto;
import ru.webbyskytracker.usersservice.dto.response.JwtAuthDto;
import ru.webbyskytracker.usersservice.dto.response.UserDtoResponse;
import ru.webbyskytracker.usersservice.dto.response.UserInfo;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.service.AuthService;
import ru.webbyskytracker.usersservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDtoResponse registerNewUser(@Valid @RequestBody RegistrationUserDto dto){
            User user = authService.initiateRegistration(dto);
            return new UserDtoResponse(
                    user.getUsername(),
                    user.getEmail(),
                    "Verification code sent to " + user.getEmail()
            );
    }

    @PostMapping("/verify-email")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.OK)
    public UserDtoResponse verifyMail(@Valid @RequestBody VerifyEmailDto verifyEmailDto){
        User user = authService.verifyMail(verifyEmailDto);
        return new UserDtoResponse(
                user.getUsername(),
                user.getEmail(),
                "Mail has been successfully confirmed"
        );
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.OK)
    public JwtAuthDto login(@Valid @RequestBody LoginRequestDto dto){
        return authService.login(dto);
    }

    @PostMapping("/refresh")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.OK)
    public JwtAuthDto refresh(@Valid @RequestBody RefreshRequestDto dto){
        return authService.refresh(dto);
    }

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public UserInfo info(){
        return userService.getUserInfo();
    }
}
