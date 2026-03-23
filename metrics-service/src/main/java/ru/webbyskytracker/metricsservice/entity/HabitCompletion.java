package ru.webbyskytracker.metricsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "habit_completions", schema = "schema_metrics")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;
    @Column(name = "completed_at", nullable = false)
    private LocalDate completedAt;
    @Column(name = "note", length = 500)
    private String note;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
