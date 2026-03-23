package ru.webbyskytracker.metricsservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users", schema = "schema_metrics")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column
    private Long id;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
