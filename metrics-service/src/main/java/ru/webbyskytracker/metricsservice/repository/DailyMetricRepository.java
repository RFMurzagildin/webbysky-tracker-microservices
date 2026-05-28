package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.webbyskytracker.metricsservice.entity.DailyMetric;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMetricRepository extends JpaRepository<DailyMetric, Long> {

    Optional<DailyMetric> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailyMetric> findByUserIdOrderByDateDesc(Long userId);
}
