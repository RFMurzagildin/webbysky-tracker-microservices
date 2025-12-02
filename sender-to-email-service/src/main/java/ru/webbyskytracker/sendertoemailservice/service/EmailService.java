package ru.webbyskytracker.sendertoemailservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender sender;

    public void sendVerificationCode(String to, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your verification code");
        message.setText("Your code: " + code + "\nValid for 2 minutes.");
        sender.send(message);
        log.info("Verification code email sent to {}", to);
    }

    public void sendEmailVerifiedNotification(String to){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email verified successfully!");
        message.setText(
                """
                        Hello!
                        
                        Your email has been successfully verified.
                        You can now log in to your WebbySky Tracker account.
                        
                        Welcome aboard!
                        — The WebbySky Tracker Team"""
        );
        sender.send(message);
        log.info("Email verified notification sent to {}", to);
    }
}
