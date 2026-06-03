package ru.webbyskytracker.aiservice.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.webbyskytracker.aiservice.entity.read.HabitCompletionReadEntity;

import java.time.LocalDate;
import java.util.List;

public interface HabitCompletionReadRepository extends JpaRepository<HabitCompletionReadEntity, Long> {

    List<HabitCompletionReadEntity> findByHabitIdInAndCompletedAtGreaterThanEqual(
            List<Long> habitIds, LocalDate from);
}
