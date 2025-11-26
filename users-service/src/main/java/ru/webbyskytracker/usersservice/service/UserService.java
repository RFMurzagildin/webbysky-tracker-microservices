package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.dto.request.VerifyEmailDto;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.exception.*;
import ru.webbyskytracker.usersservice.repository.UserRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User initiateRegistration(RegistrationUserDto dto){
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            log.info("Password and confirmation do not match");
            throw new PasswordMismatchException("Password and confirmation do not match");
        }
        if(userRepository.existsByMail(dto.getMail())){
            log.info("Email is already registered");
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.info("Username is already taken");
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        String code = generateCode();
        emailVerificationService.saveCode(dto.getMail(), code, 120);
        sendVerificationEmail(dto.getMail(), code);

        return userRepository.save(new User(
                dto.getUsername(),
                dto.getMail(),
                dto.getPassword(),
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
        if (!emailVerificationService.isValid(dto.getMail(), dto.getCode())) {
            throw new InvalidVerificationCodeException("Invalid or expired code");
        }

        User user = userRepository.findByMail(dto.getMail())
                .orElseThrow(() -> new UserNotFoundException("Registration not initiated or user not found"));

        user.setEmailVerified(true);
        emailVerificationService.deleteCode(dto.getMail());

        return userRepository.save(user);
    }



    private String generateCode(){
        return String.format("%05d", new Random().nextInt(100000));
    }
}
