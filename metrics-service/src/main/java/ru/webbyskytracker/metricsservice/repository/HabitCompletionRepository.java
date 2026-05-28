package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.metricsservice.entity.HabitCompletion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {

    boolean existsByHabitIdAndCompletedAt(Long habitId, LocalDate completedAt);

    Optional<HabitCompletion> findByHabitIdAndCompletedAt(Long habitId, LocalDate completedAt);

    @Query("SELECT c FROM HabitCompletion c WHERE c.habit.id IN :habitIds")
    List<HabitCompletion> findByHabitIdIn(@Param("habitIds") List<Long> habitIds);
}
