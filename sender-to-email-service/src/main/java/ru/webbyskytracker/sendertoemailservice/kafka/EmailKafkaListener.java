package ru.webbyskytracker.sendertoemailservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.webbyskytracker.sendertoemailservice.kafka.model.EmailVerifiedEvent;
import ru.webbyskytracker.sendertoemailservice.kafka.model.VerificationCodeEvent;
import ru.webbyskytracker.sendertoemailservice.service.EmailService;


@Component
@Slf4j
@RequiredArgsConstructor
public class EmailKafkaListener {
    private final EmailService emailService;

    @KafkaListener(
            topics = "verification-code-topic",
            groupId = "notification-group",
            containerFactory = "codeEventConcurrentKafkaListenerContainerFactory"
    )
    public void handleVerificationCode(VerificationCodeEvent event){
        log.info("Received verification code event for {}", event.getEmail());
        emailService.sendVerificationCode(event.getEmail(), event.getCode());
    }

    @KafkaListener(
            topics = "email-verified-topic",
            groupId = "notification-group",
            containerFactory = "emailVerifiedEventConcurrentKafkaListenerContainerFactory"
    )
    public void handleEmailVerified(EmailVerifiedEvent event){
        log.info("Received email verified event for {}", event.getEmail());
        emailService.sendEmailVerifiedNotification(event.getEmail());
    }
}
