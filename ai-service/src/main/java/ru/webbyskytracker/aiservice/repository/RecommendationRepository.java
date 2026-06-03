package ru.webbyskytracker.aiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.webbyskytracker.aiservice.entity.Recommendation;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    Optional<Recommendation> findTopByUserIdOrderByGeneratedAtDesc(Long userId);

    @Query("""
        SELECT COUNT(r) FROM Recommendation r
        WHERE r.userId = :userId
          AND r.generatedAt >= :startOfDay
    """)
    long countTodayByUserId(@Param("userId") Long userId,
                            @Param("startOfDay") LocalDateTime startOfDay);
}
