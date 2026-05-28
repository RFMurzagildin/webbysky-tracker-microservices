package ru.webbyskytracker.metricsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.metricsservice.dto.request.SaveDailyMetricDto;
import ru.webbyskytracker.metricsservice.dto.response.DailyMetricResponseDto;
import ru.webbyskytracker.metricsservice.service.DailyMetricService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics/daily")
@RequiredArgsConstructor
public class DailyMetricController {

    private final DailyMetricService service;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DailyMetricResponseDto save(
            @AuthenticationPrincipal Long userId,
            @RequestBody SaveDailyMetricDto dto
    ) {
        return service.save(userId, dto);
    }

    @GetMapping("/today")
    public DailyMetricResponseDto getToday(@AuthenticationPrincipal Long userId) {
        return service.getToday(userId);
    }

    @GetMapping
    public DailyMetricResponseDto getForDate(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getForDate(userId, date);
    }

    @GetMapping("/all")
    public List<DailyMetricResponseDto> getAll(@AuthenticationPrincipal Long userId) {
        return service.getAll(userId);
    }
}
