package ru.webbyskytracker.metricsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.metricsservice.dto.request.CreateHabitDto;
import ru.webbyskytracker.metricsservice.dto.response.HabitResponseDto;
import ru.webbyskytracker.metricsservice.entity.Habit;
import ru.webbyskytracker.metricsservice.exception.HabitAlreadyExistsException;
import ru.webbyskytracker.metricsservice.exception.InvalidTokenException;
import ru.webbyskytracker.metricsservice.repository.HabitRepository;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final JwtService jwtService;

    public HabitResponseDto createHabit(String token, CreateHabitDto dto){
        //Проверка на валидность токена
        if(!jwtService.validateToken(token)){
            throw new InvalidTokenException("Token is invalid or expired");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        //Проверка существования такой привычки
        if(habitRepository.existsByUserIdAndName(userId, dto.getName())){
            throw new HabitAlreadyExistsException("Habit with this name already exists");
        }
        Habit saved = habitRepository.save(
                Habit.builder()
                        .userId(jwtService.getUserIdFromToken(token))
                        .name(dto.getName())
                        .color(dto.getColor())
                        .isActive(true)
                .build());

        return HabitResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .message("Habit was created")
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
