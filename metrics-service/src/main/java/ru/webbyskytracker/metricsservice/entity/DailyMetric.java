package ru.webbyskytracker.metricsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "daily_metrics",
    schema = "schema_metrics",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "sleep_hours")
    private Float sleepHours;

    @Column(name = "mood")
    private Integer mood;

    @Column(name = "productivity")
    private Integer productivity;

    @Column(name = "energy")
    private Integer energy;

    @Column(name = "water_glasses")
    private Integer waterGlasses;

    @Column(name = "exercise_minutes")
    private Integer exerciseMinutes;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
