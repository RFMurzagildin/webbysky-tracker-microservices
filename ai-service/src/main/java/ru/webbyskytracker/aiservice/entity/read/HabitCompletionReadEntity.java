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
@Table(name = "habit_completions", schema = "schema_metrics")
@Getter
public class HabitCompletionReadEntity {

    @Id
    private Long id;

    @Column(name = "habit_id")
    private Long habitId;

    @Column(name = "completed_at")
    private LocalDate completedAt;
}
