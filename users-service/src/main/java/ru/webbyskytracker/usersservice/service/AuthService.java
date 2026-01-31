package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
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

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, VerificationCodeEvent> verificationCodeKafkaTemplate;
    private final KafkaTemplate<String,  EmailVerifiedEvent > emailVerifiedKafkaTemplate;
    @Value("${jwt.refresh-token.expiration-days:7}")
    private long refreshTokenExpirationDays;

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
        //думаю, тут можно улучшить логику проверки кода
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

    public JwtAuthDto login(LoginRequestDto loginDto){
        //проверяем, существует ли пользователь с данной почтой
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        //проверяем, подтвердил ли пользователь свою почту
        if (!user.getEmailVerified()) {
            throw new UserNotVerifiedException("Email not verified");
        }

        //проверяем, сходятся ли пароли, который ввел пользователь и который хранится в БД
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid email or password");
        }

        //если все проверки пройдены, то генерируются токены
        JwtAuthDto jwtAuthDto = jwtService.generateAuthToken(user.getEmail(), user.getId(), user.getRole().name());
        refreshTokenService.save(jwtAuthDto.getRefreshToken(), user.getId(), Duration.ofDays(refreshTokenExpirationDays));
        return jwtAuthDto;
    }

    public JwtAuthDto refresh(RefreshRequestDto dto){
        String refreshToken = dto.getRefreshToken();
        //проверка валидности токена
        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        //проверка наличия токена в Redis
        if(!refreshTokenService.exists(refreshToken)){
            throw new InvalidRefreshTokenException("Refresh token not found in Redis");
        }

        String email = jwtService.getEmailFromToken(dto.getRefreshToken());
        String userId = String.valueOf(jwtService.getUserIdFromToken(dto.getRefreshToken()));
        String role = jwtService.getRoleFromToken(dto.getRefreshToken());

        //Доп.проверка соответствия пользователя
        if (!userId.equals(refreshTokenService.getUserId(refreshToken))) {
            throw new InvalidRefreshTokenException("User ID mismatch in refresh token");
        }

        JwtAuthDto newTokens = jwtService.refreshAuthToken(email, Long.valueOf(userId), role);

        //Удаляем из Redis старый токен и обновляем его новым
        refreshTokenService.delete(refreshToken);
        refreshTokenService.save(newTokens.getRefreshToken(), Long.valueOf(userId), Duration.ofDays(refreshTokenExpirationDays));

        return newTokens;
    }

    public void logout(String authorizationHeader){
        String refreshToken = extractTokenFromHeader(authorizationHeader);
        if (refreshTokenService.exists(refreshToken)) {
            refreshTokenService.delete(refreshToken);
        }
    }

    // Вспомогательный метод для извлечения токена
    private String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }
}
