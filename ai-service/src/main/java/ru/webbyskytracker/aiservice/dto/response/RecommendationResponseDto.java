package ru.webbyskytracker.aiservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationResponseDto {
    private Long id;
    private LocalDateTime generatedAt;
    private String modelUsed;
    private String content;
    private boolean isToday;
    private long generatedTodayCount;
    private int dailyLimit;
}
