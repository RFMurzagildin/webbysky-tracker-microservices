package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.metricsservice.entity.HabitCompletion;

import java.time.LocalDate;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {

    boolean existsByHabitIdAndCompletedAt(Long habitId, LocalDate completedAt);
}
