package ru.webbyskytracker.aiservice.entity.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Table(name = "habits", schema = "schema_metrics")
@Getter
public class HabitReadEntity {

    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String name;

    @Column(name = "is_active")
    private Boolean isActive;
}
