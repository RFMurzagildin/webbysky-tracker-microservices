package ru.webbyskytracker.usersservice.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;
import ru.webbyskytracker.usersservice.kafka.model.EmailVerifiedEvent;
import ru.webbyskytracker.usersservice.kafka.model.VerificationCodeEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, VerificationCodeEvent> verificationCodeEventProducerFactory(ObjectMapper objectMapper){
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JsonSerializer<VerificationCodeEvent> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new StringSerializer(),
                serializer
        );
    }

    @Bean
    public KafkaTemplate<String, VerificationCodeEvent> verificationCodeEventKafkaTemplate(
            ProducerFactory<String, VerificationCodeEvent> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, EmailVerifiedEvent> emailVerifiedProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JsonSerializer<EmailVerifiedEvent> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                serializer
        );
    }

    @Bean
    public KafkaTemplate<String, EmailVerifiedEvent> emailVerifiedKafkaTemplate(
            ProducerFactory<String, EmailVerifiedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
