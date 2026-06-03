package ru.webbyskytracker.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${ai.lm-studio.timeout-seconds:120}")
    private int timeoutSeconds;

    @Bean
    public RestTemplate lmStudioRestTemplate(RestTemplateBuilder builder) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        return builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(timeout)
                .build();
    }
}
