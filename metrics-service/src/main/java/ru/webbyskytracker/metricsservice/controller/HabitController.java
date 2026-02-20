package ru.webbyskytracker.metricsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.metricsservice.dto.request.CreateHabitDto;
import ru.webbyskytracker.metricsservice.dto.response.HabitResponseDto;
import ru.webbyskytracker.metricsservice.service.HabitService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics/habits")
public class HabitController {
    private final HabitService habitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponseDto createHabit(
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody CreateHabitDto habit
    ){
        String token = accessToken.replace("Bearer ", "");
        return habitService.createHabit(token, habit);
    }
}
