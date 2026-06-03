package ru.webbyskytracker.aiservice.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.webbyskytracker.aiservice.entity.read.DailyMetricReadEntity;

import java.time.LocalDate;
import java.util.List;

public interface DailyMetricReadRepository extends JpaRepository<DailyMetricReadEntity, Long> {

    List<DailyMetricReadEntity> findByUserIdAndDateGreaterThanEqualOrderByDateDesc(
            Long userId, LocalDate from);
}
