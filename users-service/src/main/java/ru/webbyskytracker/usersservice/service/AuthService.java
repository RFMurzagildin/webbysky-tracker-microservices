package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.request.*;
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
    private static final String VERIFY_CODE_PREFIX = "verification:";
    private static final String RESET_PASSWORD_PREFIX = "reset-password";

    public User register(RegistrationUserDto dto){
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        String code = emailVerificationService.generateNewCode();
        emailVerificationService.saveCodeInRedis(VERIFY_CODE_PREFIX, dto.getEmail(), code, 120);

        VerificationCodeEvent event = new VerificationCodeEvent(dto.getEmail(), code);
        verificationCodeKafkaTemplate.send("verification-code-topic", event);

        return userRepository.save(new User(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                false
        ));
    }

    public User verifyMail(VerifyEmailDto dto){
        String email = dto.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Registration not initiated or user not found"));

        if (!emailVerificationService.isValid(VERIFY_CODE_PREFIX, email, dto.getCode())) {
            throw new InvalidVerificationCodeException("Invalid or expired code");
        }

        user.setEmailVerified(true);

        emailVerificationService.deleteCodeFromRedis(VERIFY_CODE_PREFIX, email);

        EmailVerifiedEvent event = new EmailVerifiedEvent(email);
        emailVerifiedKafkaTemplate.send("email-verified-topic",event);
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

    public void initiatePasswordReset(String email){
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String resetCode = emailVerificationService.generateNewCode();
        //Сохраняем код в Redis
        emailVerificationService.saveCodeInRedis(RESET_PASSWORD_PREFIX, email, resetCode, 120);
        //Отправляем событие в Kafka
        VerificationCodeEvent event = new VerificationCodeEvent(email, resetCode);
        verificationCodeKafkaTemplate.send("reset-password-code-topic", event);
        log.info("Password reset code sent for {}", email);
    }

    public void resetPassword(ResetPasswordDto dto){
        //Проверка на совпадение паролей
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())){
            throw new PasswordMismatchException("New password and confirmation do not match");
        }
        if(!emailVerificationService.isValid(RESET_PASSWORD_PREFIX, dto.getEmail(), dto.getCode())){
            throw new InvalidVerificationCodeException("Invalid or expired reset code");
        }
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        //Обновляем пароль
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        //Удаляем код из Redis
        emailVerificationService.deleteCodeFromRedis("reset-password:", dto.getEmail());

        log.info("Password successfully reset for {}", dto.getEmail());
    }
}
