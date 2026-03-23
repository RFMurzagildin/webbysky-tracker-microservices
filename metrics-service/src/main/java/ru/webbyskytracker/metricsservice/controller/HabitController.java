package ru.webbyskytracker.metricsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.metricsservice.dto.request.CreateHabitDto;
import ru.webbyskytracker.metricsservice.dto.request.UpdateHabitDto;
import ru.webbyskytracker.metricsservice.dto.response.HabitResponseDto;
import ru.webbyskytracker.metricsservice.service.HabitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics/habits")
public class HabitController {

    private final HabitService habitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponseDto createHabit(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateHabitDto habit
    ){
        return habitService.createHabit(userId, habit);
    }

    @GetMapping
    public List<HabitResponseDto> getAllHabits(
            @AuthenticationPrincipal Long userId
    ){
        return habitService.getAllHabits(userId);
    }

    @GetMapping("/{id}")
    public HabitResponseDto getHabitById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ){
        return habitService.getHabitById(id, userId);
    }

    @PutMapping("/{id}")
    public HabitResponseDto updateHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateHabitDto dto
    ){
        return habitService.updateHabit(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ){
        habitService.deleteHabit(id, userId);
    }
}
