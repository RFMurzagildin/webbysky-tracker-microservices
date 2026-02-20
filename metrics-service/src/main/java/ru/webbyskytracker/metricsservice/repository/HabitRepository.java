package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.metricsservice.entity.Habit;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
}
