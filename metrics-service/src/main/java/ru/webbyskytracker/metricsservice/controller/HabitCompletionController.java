package ru.webbyskytracker.metricsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.metricsservice.dto.request.CreateCompletionDto;
import ru.webbyskytracker.metricsservice.dto.response.CompletionResponseDto;
import ru.webbyskytracker.metricsservice.service.HabitCompletionService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics/completions")
public class HabitCompletionController {

    private final HabitCompletionService completionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletionResponseDto createCompletion(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateCompletionDto dto
    ) {
        return completionService.createCompletion(userId, dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompletion(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        completionService.deleteCompletion(userId, habitId, date);
    }
}
