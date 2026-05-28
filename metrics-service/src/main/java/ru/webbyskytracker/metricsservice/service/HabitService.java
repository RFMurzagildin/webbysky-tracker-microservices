package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.metricsservice.dto.request.CreateHabitDto;
import ru.webbyskytracker.metricsservice.dto.request.UpdateHabitDto;
import ru.webbyskytracker.metricsservice.dto.response.HabitResponseDto;
import ru.webbyskytracker.metricsservice.entity.Habit;
import ru.webbyskytracker.metricsservice.entity.HabitCompletion;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyExistsException;
import ru.webbyskytracker.metricsservice.exception.HabitNotFoundException;
import ru.webbyskytracker.metricsservice.repository.HabitCompletionRepository;
import ru.webbyskytracker.metricsservice.repository.HabitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;

    public HabitResponseDto createHabit(Long userId, CreateHabitDto dto) {
        if (habitRepository.existsByUserIdAndName(userId, dto.getName())) {
            throw new HabitAlreadyExistsException("Habit with this name already exists");
        }
        Habit saved = habitRepository.save(
                Habit.builder()
                        .userId(userId)
                        .name(dto.getName())
                        .color(dto.getColor())
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build());

        return toDto(saved, List.of());
    }

    public List<HabitResponseDto> getAllHabits(Long userId) {
        List<Habit> habits = habitRepository.findByUserId(userId);
        if (habits.isEmpty()) return List.of();

        List<Long> habitIds = habits.stream().map(Habit::getId).toList();
        List<HabitCompletion> allCompletions = completionRepository.findByHabitIdIn(habitIds);

        Map<Long, List<HabitCompletion>> byHabitId = allCompletions.stream()
                .collect(Collectors.groupingBy(c -> c.getHabit().getId()));

        return habits.stream()
                .map(h -> toDto(h, byHabitId.getOrDefault(h.getId(), List.of())))
                .toList();
    }

    public HabitResponseDto getHabitById(Long id, Long userId) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        List<HabitCompletion> completions = completionRepository.findByHabitIdIn(List.of(habit.getId()));
        return toDto(habit, completions);
    }

    public HabitResponseDto updateHabit(Long id, Long userId, UpdateHabitDto dto) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        if (!dto.getName().equals(habit.getName())) {
            if (habitRepository.existsByUserIdAndName(userId, dto.getName())) {
                throw new HabitAlreadyExistsException("Habit with this name already exists");
            }
            habit.setName(dto.getName());
        }

        habit.setColor(dto.getColor());
        habit.setIsActive(dto.getIsActive());

        Habit saved = habitRepository.save(habit);
        List<HabitCompletion> completions = completionRepository.findByHabitIdIn(List.of(saved.getId()));
        return toDto(saved, completions);
    }

    public void deleteHabit(Long id, Long userId) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        habitRepository.deleteById(habit.getId());
    }

    private HabitResponseDto toDto(Habit habit, List<HabitCompletion> completions) {
        List<LocalDate> completedDates = completions.stream()
                .map(HabitCompletion::getCompletedAt)
                .sorted(Comparator.reverseOrder())
                .toList();

        return HabitResponseDto.builder()
                .id(habit.getId())
                .userId(habit.getUserId())
                .name(habit.getName())
                .color(habit.getColor())
                .isActive(habit.getIsActive())
                .createdAt(habit.getCreatedAt())
                .streak(calculateStreak(completedDates))
                .completedDates(completedDates)
                .build();
    }

    private int calculateStreak(List<LocalDate> sortedDatesDesc) {
        if (sortedDatesDesc.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate mostRecent = sortedDatesDesc.get(0);
        if (mostRecent.isBefore(today.minusDays(1))) return 0;

        int streak = 0;
        LocalDate expected = mostRecent;
        for (LocalDate date : sortedDatesDesc) {
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
