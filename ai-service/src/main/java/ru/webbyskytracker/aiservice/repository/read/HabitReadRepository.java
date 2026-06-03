package ru.webbyskytracker.aiservice.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.webbyskytracker.aiservice.entity.read.HabitReadEntity;

import java.util.List;

public interface HabitReadRepository extends JpaRepository<HabitReadEntity, Long> {
    List<HabitReadEntity> findByUserId(Long userId);
}
