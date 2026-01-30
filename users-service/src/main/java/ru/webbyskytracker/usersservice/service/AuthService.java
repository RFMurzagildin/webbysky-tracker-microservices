package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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
import ru.webbyskytracker.usersservice.kafka.model.EmailVerifiedEvent;
import ru.webbyskytracker.usersservice.kafka.model.VerificationCodeEvent;
import ru.webbyskytracker.usersservice.repository.UserRepository;
import ru.webbyskytracker.usersservice.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, VerificationCodeEvent> verificationCodeKafkaTemplate;
    private final KafkaTemplate<String,  EmailVerifiedEvent > emailVerifiedKafkaTemplate;

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

        //Публикуем событие в Kafka
        VerificationCodeEvent event = new VerificationCodeEvent(dto.getEmail(), code);
        verificationCodeKafkaTemplate.send("verification-code-topic", event);
        log.info("Published verification code event for {}", dto.getEmail());

        return userRepository.save(new User(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                false
        ));
    }

    public User verifyMail(VerifyEmailDto dto){
        String email = dto.getEmail();
        if (!emailVerificationService.isValid(email, dto.getCode())) {
            throw new InvalidVerificationCodeException("Invalid or expired code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Registration not initiated or user not found"));

        user.setEmailVerified(true);

        emailVerificationService.deleteCodeFromRedis(email);

        //отправляем событие в Kafka
        EmailVerifiedEvent event = new EmailVerifiedEvent(email);
        emailVerifiedKafkaTemplate.send("email-verified-topic",event);
        log.info("Published email verified event for {}", user.getEmail());
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

        // Здесь в будущем будем проверять, что refresh token есть в Redis
        // if (!refreshTokenStore.exists(refreshToken)) { throw ... }

        return jwtService.refreshAuthToken(email, userId, role);
    }
}
