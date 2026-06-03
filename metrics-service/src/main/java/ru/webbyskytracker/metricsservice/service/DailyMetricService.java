package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.webbyskytracker.metricsservice.dto.request.SaveDailyMetricDto;
import ru.webbyskytracker.metricsservice.dto.response.DailyMetricResponseDto;
import ru.webbyskytracker.metricsservice.entity.DailyMetric;
import ru.webbyskytracker.metricsservice.exception.DailyMetricNotFoundException;
import ru.webbyskytracker.metricsservice.repository.DailyMetricRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyMetricService {

    private final DailyMetricRepository repository;

    @Transactional
    public DailyMetricResponseDto save(Long userId, SaveDailyMetricDto dto) {
        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        DailyMetric metric = repository.findByUserIdAndDate(userId, date)
                .orElse(DailyMetric.builder().userId(userId).date(date).build());

        if (dto.getSleepHours()      != null) metric.setSleepHours(dto.getSleepHours());
        if (dto.getMood()            != null) metric.setMood(dto.getMood());
        if (dto.getProductivity()    != null) metric.setProductivity(dto.getProductivity());
        if (dto.getEnergy()          != null) metric.setEnergy(dto.getEnergy());
        if (dto.getWaterGlasses()    != null) metric.setWaterGlasses(dto.getWaterGlasses());
        if (dto.getExerciseMinutes() != null) metric.setExerciseMinutes(dto.getExerciseMinutes());
        if (dto.getNote()            != null) metric.setNote(dto.getNote());

        return toDto(repository.save(metric));
    }

    @Transactional(readOnly = true)
    public DailyMetricResponseDto getForDate(Long userId, LocalDate date) {
        return repository.findByUserIdAndDate(userId, date)
                .map(this::toDto)
                .orElseThrow(() -> new DailyMetricNotFoundException(
                        "No metrics found for date " + date));
    }

    @Transactional(readOnly = true)
    public DailyMetricResponseDto getToday(Long userId) {
        return getForDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<DailyMetricResponseDto> getAll(Long userId) {
        return repository.findByUserIdOrderByDateDesc(userId)
                .stream().map(this::toDto).toList();
    }

    private DailyMetricResponseDto toDto(DailyMetric m) {
        return DailyMetricResponseDto.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .date(m.getDate())
                .sleepHours(m.getSleepHours())
                .mood(m.getMood())
                .productivity(m.getProductivity())
                .energy(m.getEnergy())
                .waterGlasses(m.getWaterGlasses())
                .exerciseMinutes(m.getExerciseMinutes())
                .note(m.getNote())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
