package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.metricsservice.dto.request.CreateHabitDto;
import ru.webbyskytracker.metricsservice.dto.request.UpdateHabitDto;
import ru.webbyskytracker.metricsservice.dto.response.HabitResponseDto;
import ru.webbyskytracker.metricsservice.entity.Habit;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyExistsException;
import ru.webbyskytracker.metricsservice.exception.HabitNotFoundException;
import ru.webbyskytracker.metricsservice.exception.InvalidTokenException;
import ru.webbyskytracker.metricsservice.repository.HabitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final JwtService jwtService;

    public HabitResponseDto createHabit(String token, CreateHabitDto dto){
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        if(habitRepository.existsByUserIdAndName(userId, dto.getName())){
            throw new HabitAlreadyExistsException("Habit with this name already exists");
        }
        Habit saved = habitRepository.save(
                Habit.builder()
                        .userId(jwtService.getUserIdFromToken(token))
                        .name(dto.getName())
                        .color(dto.getColor())
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                .build());
        return toDto(saved);
    }

    public List<HabitResponseDto> getAllHabits(String token){
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        List<Habit> habits = habitRepository.findByUserId(userId);
        return habits.stream().map(this::toDto).toList();
    }

    public HabitResponseDto getHabitById(Long id, String token){
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        return toDto(habit);
    }

    public HabitResponseDto updateHabit(Long id, String token, UpdateHabitDto dto){
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        if(dto.getName() == null || dto.getColor() == null || dto.getIsActive() == null){
            throw new NullPointerException("The field is null");
        }
        if(habitRepository.existsByUserIdAndName(userId, dto.getName())){
            throw new HabitAlreadyExistsException("Habit with this name already exists");
        }
        habit.setName(dto.getName());
        habit.setColor(dto.getColor());
        habit.setIsActive(dto.getIsActive());

        return toDto(habitRepository.save(habit));
    }

    public void deleteHabit(Long id, String token){
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        habitRepository.deleteById(habit.getId());
    }


    private HabitResponseDto toDto(Habit habit){
        return HabitResponseDto.builder()
                .id(habit.getId())
                .userId(habit.getUserId())
                .name(habit.getName())
                .color(habit.getColor())
                .isActive(habit.getIsActive())
                .createdAt(habit.getCreatedAt())
                .build();
    }
}
