package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.request.LoginRequestDto;
import ru.webbyskytracker.usersservice.dto.request.RefreshRequestDto;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.dto.request.VerifyEmailDto;
import ru.webbyskytracker.usersservice.dto.response.JwtAuthDto;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.exception.*;
import ru.webbyskytracker.usersservice.repository.UserRepository;
import ru.webbyskytracker.usersservice.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User initiateRegistration(RegistrationUserDto dto){
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            log.info("Password and confirmation do not match");
            throw new PasswordMismatchException("Password and confirmation do not match");
        }
        if(userRepository.existsByEmail(dto.getEmail())){
            log.info("Email is already registered");
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.info("Username is already taken");
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        String code = emailVerificationService.generateNewCode();
        emailVerificationService.saveCodeInRedis(dto.getEmail(), code, 120);
        sendVerificationEmail(dto.getEmail(), code);

        return userRepository.save(new User(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                false
        ));
    }

    public void sendVerificationEmail(String to, String code){
        SimpleMailMessage mes = new SimpleMailMessage();
        mes.setTo(to);
        mes.setSubject("Your verification code");
        mes.setText("Your code: " + code + "\nValid for 2 minutes.");
        mailSender.send(mes);
        log.info("Confirmation code has been sent to {}", to);
    }

    public User verifyMail(VerifyEmailDto dto){
        if (!emailVerificationService.isValid(dto.getEmail(), dto.getCode())) {
            throw new InvalidVerificationCodeException("Invalid or expired code");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Registration not initiated or user not found"));

        user.setEmailVerified(true);
        emailVerificationService.deleteCodeFromRedis(dto.getEmail());

        return userRepository.save(user);
    }

    public JwtAuthDto login(LoginRequestDto loginDto) throws AuthenticationException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        if (!user.getEmailVerified()) {
            throw new UserNotVerifiedException("Email not verified");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid email or password");
        }

        return jwtService.generateAuthToken(user.getEmail(), user.getId(), user.getRole().name());
    }

    public JwtAuthDto refresh(RefreshRequestDto dto){
        if (!jwtService.validateToken(dto.getRefreshToken())) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(dto.getRefreshToken());
        Long userId = jwtService.getUserIdFromToken(dto.getRefreshToken());
        String role = jwtService.getRoleFromToken(dto.getRefreshToken());

        // Здесь в будущем будем проверяем, что refresh token есть в Redis
        // if (!refreshTokenStore.exists(refreshToken)) { throw ... }

        return jwtService.refreshAuthToken(email, userId, role);
    }
}
