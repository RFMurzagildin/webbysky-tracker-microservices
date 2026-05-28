package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.metricsservice.dto.request.CreateCompletionDto;
import ru.webbyskytracker.metricsservice.dto.response.CompletionResponseDto;
import ru.webbyskytracker.metricsservice.entity.Habit;
import ru.webbyskytracker.metricsservice.entity.HabitCompletion;
import ru.webbyskytracker.metricsservice.exception.CompletionNotFoundException;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyCompletedException;
import ru.webbyskytracker.metricsservice.exception.HabitNotFoundException;
import ru.webbyskytracker.metricsservice.repository.HabitCompletionRepository;
import ru.webbyskytracker.metricsservice.repository.HabitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HabitCompletionService {

    private final HabitCompletionRepository completionRepository;
    private final HabitRepository habitRepository;

    public CompletionResponseDto createCompletion(Long userId, CreateCompletionDto dto) {
        Habit habit = habitRepository.findByIdAndUserId(dto.getHabitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        if (completionRepository.existsByHabitIdAndCompletedAt(habit.getId(), dto.getCompletedAt())) {
            throw new HabitAlreadyCompletedException("Habit already completed for this date");
        }

        HabitCompletion completion = completionRepository.save(
                HabitCompletion.builder()
                        .habit(habit)
                        .completedAt(dto.getCompletedAt())
                        .note(dto.getNote())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return toDto(completion);
    }

    public void deleteCompletion(Long userId, Long habitId, LocalDate completedAt) {
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        HabitCompletion completion = completionRepository
                .findByHabitIdAndCompletedAt(habit.getId(), completedAt)
                .orElseThrow(() -> new CompletionNotFoundException("Completion not found"));

        completionRepository.delete(completion);
    }

    private CompletionResponseDto toDto(HabitCompletion completion) {
        return CompletionResponseDto.builder()
                .id(completion.getId())
                .habitId(completion.getHabit().getId())
                .habitName(completion.getHabit().getName())
                .completedAt(completion.getCompletedAt())
                .note(completion.getNote())
                .createdAt(completion.getCreatedAt())
                .build();
    }
}
