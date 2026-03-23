package ru.webbyskytracker.metricsservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.webbyskytracker.metricsservice.entity.User;
import ru.webbyskytracker.metricsservice.kafka.model.UserRegisteredEvent;
import ru.webbyskytracker.metricsservice.repository.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserRepository userRepository;

    @RetryableTopic(
        backoff = @Backoff(delay = 1000),
        dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
        topics = "user-registered-topic",
        groupId = "metrics-service-group",
        containerFactory = "codeEventConcurrentKafkaListenerContainerFactory"
    )
    public void handlerUserRegistered(UserRegisteredEvent event){
        if(userRepository.existsById(event.getUserId())){
            log.warn("User already exists in metrics-service: userId={}", event.getUserId());
        }

        User user = User.builder()
                .id(event.getUserId())
                .email(event.getEmail())
                .username(event.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("User created in metrics-service: userId={}, email={}",
                event.getUserId(), event.getEmail());
    }

}
