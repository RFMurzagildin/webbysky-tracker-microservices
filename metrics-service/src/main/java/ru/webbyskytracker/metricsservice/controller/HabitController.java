package ru.webbyskytracker.metricsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody CreateHabitDto habit
    ){
        String token = accessToken.replace("Bearer ", "");
        return habitService.createHabit(token, habit);
    }

    @GetMapping
    public List<HabitResponseDto> getAllHabits(
            @RequestHeader("Authorization") String accessToken
    ){
        String token = accessToken.replace("Bearer ", "");
        return habitService.getAllHabits(token);
    }

    @GetMapping("/{id}")
    public HabitResponseDto getHabitById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken
    ){
        String token = accessToken.replace("Bearer ", "");
        return habitService.getHabitById(id, token);
    }

    @PutMapping("/{id}")
    public HabitResponseDto updateHabit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody UpdateHabitDto dto
    ){
        String token = accessToken.replace("Bearer ", "");
        return habitService.updateHabit(id, token, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken
    ){
        String token = accessToken.replace("Bearer ", "");
        habitService.deleteHabit(id, token);
    }
}
