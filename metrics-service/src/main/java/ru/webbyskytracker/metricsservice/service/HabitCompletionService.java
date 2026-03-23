package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.metricsservice.dto.request.CreateCompletionDto;
import ru.webbyskytracker.metricsservice.dto.response.CompletionResponseDto;
import ru.webbyskytracker.metricsservice.entity.Habit;
import ru.webbyskytracker.metricsservice.entity.HabitCompletion;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyCompletedException;
import ru.webbyskytracker.metricsservice.exception.HabitNotFoundException;
import ru.webbyskytracker.metricsservice.repository.HabitCompletionRepository;
import ru.webbyskytracker.metricsservice.repository.HabitRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HabitCompletionService {

    private final HabitCompletionRepository completionRepository;
    private final JwtService jwtService;
    private final HabitRepository habitRepository;

    public CompletionResponseDto createCompletion(String token, CreateCompletionDto dto){
        Long userId = jwtService.getUserIdFromToken(token);

        Habit habit = habitRepository.findByIdAndUserId(dto.getHabitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        if(completionRepository.existsByHabitIdAndCompletedAt(habit.getId(), dto.getCompletedAt())){
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

    /*public List<CompletionResponseDto> getCompletions(String token, LocalDate startDate, LocalDate endDate){
        Long userId = jwtService.getUserIdFromToken(token);


    }

    public List<CompletionResponseDto> getHabitCompletions(Long habitId, String token){
        Long userId = jwtService.getUserIdFromToken(token);

    }

    public void deleteCompletion(Long completionId, String token){
        Long userId = jwtService.getUserIdFromToken(token);

    }*/

    private CompletionResponseDto toDto(HabitCompletion completion){
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
