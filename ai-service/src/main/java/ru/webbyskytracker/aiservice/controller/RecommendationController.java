package ru.webbyskytracker.aiservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.aiservice.dto.response.RecommendationResponseDto;
import ru.webbyskytracker.aiservice.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/ai/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping("/latest")
    public ResponseEntity<RecommendationResponseDto> getLatest(
            @AuthenticationPrincipal Long userId) {
        return service.getLatest(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationResponseDto generate(@AuthenticationPrincipal Long userId) {
        return service.generate(userId);
    }

    @PostMapping("/force")
    public RecommendationResponseDto forceRegenerate(@AuthenticationPrincipal Long userId) {
        return service.forceRegenerate(userId);
    }
}
