package ru.webbyskytracker.aiservice.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.webbyskytracker.aiservice.exception.LlmUnavailableException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LmStudioClient {

    private final RestTemplate lmStudioRestTemplate;

    @Value("${ai.lm-studio.url}")
    private String lmStudioUrl;

    @Value("${ai.lm-studio.model}")
    private String model;

    @Value("${ai.lm-studio.max-tokens:1500}")
    private int maxTokens;

    @Value("${ai.lm-studio.temperature:0.7}")
    private double temperature;


    @Data
    static class ChatRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        @JsonProperty("max_tokens")
        private Integer maxTokens;

        @Data
        static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    static class ChatResponse {
        private List<Choice> choices;

        @Data
        static class Choice {
            private Message message;

            @Data
            static class Message {
                private String content;
            }
        }
    }

    public String chat(String systemPrompt, String userPrompt) {
        String endpoint = lmStudioUrl + "/v1/chat/completions";

        ChatRequest.Message sysMsg = new ChatRequest.Message();
        sysMsg.setRole("system");
        sysMsg.setContent(systemPrompt);

        ChatRequest.Message userMsg = new ChatRequest.Message();
        userMsg.setRole("user");
        userMsg.setContent(userPrompt);

        ChatRequest requestBody = new ChatRequest();
        requestBody.setModel(model);
        requestBody.setMessages(List.of(sysMsg, userMsg));
        requestBody.setTemperature(temperature);
        requestBody.setMaxTokens(maxTokens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Sending prompt to LM Studio (model: {})", model);
            ResponseEntity<ChatResponse> response =
                    lmStudioRestTemplate.postForEntity(endpoint, entity, ChatResponse.class);

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && !response.getBody().getChoices().isEmpty()) {

                String content = response.getBody().getChoices().get(0).getMessage().getContent();
                log.info("LM Studio responded ({} chars)", content.length());
                return content;
            }

            throw new LlmUnavailableException("LM Studio returned empty response");

        } catch (RestClientException ex) {
            log.error("Failed to reach LM Studio at {}: {}", endpoint, ex.getMessage());
            throw new LlmUnavailableException(
                    "LM Studio недоступен. Убедитесь, что сервер запущен.", ex);
        }
    }

    public String getModel() { return model; }
}
