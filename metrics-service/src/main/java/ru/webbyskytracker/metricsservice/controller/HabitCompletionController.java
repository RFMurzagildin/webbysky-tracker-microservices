package ru.webbyskytracker.metricsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.metricsservice.dto.request.CreateCompletionDto;
import ru.webbyskytracker.metricsservice.dto.response.CompletionResponseDto;
import ru.webbyskytracker.metricsservice.service.HabitCompletionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics/completions")
public class HabitCompletionController {

    private final HabitCompletionService completionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletionResponseDto createCompletion(
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody CreateCompletionDto dto
    ){
        String token = accessToken.replace("Bearer ", "");
        return completionService.createCompletion(token, dto);
    }
}
