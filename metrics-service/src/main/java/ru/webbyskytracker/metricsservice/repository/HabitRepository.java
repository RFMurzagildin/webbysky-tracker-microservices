package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.metricsservice.entity.Habit;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    boolean existsByUserIdAndName(Long userId, String name);
    List<Habit> findByUserId(Long userId);
    Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
