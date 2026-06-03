package ru.webbyskytracker.aiservice.entity.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "daily_metrics", schema = "schema_metrics")
@Getter
public class DailyMetricReadEntity {

    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private LocalDate date;

    @Column(name = "sleep_hours")
    private Float sleepHours;

    private Integer mood;
    private Integer productivity;
    private Integer energy;

    @Column(name = "water_glasses")
    private Integer waterGlasses;

    @Column(name = "exercise_minutes")
    private Integer exerciseMinutes;
}
