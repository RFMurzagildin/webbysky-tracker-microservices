package ru.webbyskytracker.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.webbyskytracker.aiservice.dto.response.RecommendationResponseDto;
import ru.webbyskytracker.aiservice.entity.Recommendation;
import ru.webbyskytracker.aiservice.exception.RateLimitException;
import ru.webbyskytracker.aiservice.repository.RecommendationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository repo;
    private final ContextBuilderService    contextBuilder;
    private final LmStudioClient           llm;

    @Value("${ai.recommendation.daily-limit:1}")
    private int dailyLimit;

    @Transactional(readOnly = true)
    public Optional<RecommendationResponseDto> getLatest(Long userId) {
        return repo.findTopByUserIdOrderByGeneratedAtDesc(userId)
                .map(r -> toDto(r, userId));
    }

    @Transactional
    public RecommendationResponseDto generate(Long userId) {
        long todayCount = countToday(userId);
        if (todayCount >= dailyLimit) {
            throw new RateLimitException(
                    "Лимит рекомендаций на сегодня исчерпан (" + dailyLimit + "/" + dailyLimit + "). " +
                    "Следующий совет будет доступен завтра.");
        }
        return doGenerate(userId);
    }

    @Transactional
    public RecommendationResponseDto forceRegenerate(Long userId) {
        return doGenerate(userId);
    }


    private RecommendationResponseDto doGenerate(Long userId) {
        log.info("Generating recommendation for userId={}", userId);

        String systemPrompt = contextBuilder.systemPrompt();
        String userPrompt   = contextBuilder.buildUserPrompt(userId);
        String content      = llm.chat(systemPrompt, userPrompt);

        Recommendation saved = repo.save(
                Recommendation.builder()
                        .userId(userId)
                        .generatedAt(LocalDateTime.now())
                        .modelUsed(llm.getModel())
                        .content(content)
                        .build()
        );

        log.info("Recommendation {} saved for userId={}", saved.getId(), userId);
        return toDto(saved, userId);
    }

    private long countToday(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return repo.countTodayByUserId(userId, startOfDay);
    }

    private RecommendationResponseDto toDto(Recommendation r, Long userId) {
        long todayCount = countToday(userId);
        boolean isToday = r.getGeneratedAt().toLocalDate().equals(LocalDate.now());
        return RecommendationResponseDto.builder()
                .id(r.getId())
                .generatedAt(r.getGeneratedAt())
                .modelUsed(r.getModelUsed())
                .content(r.getContent())
                .isToday(isToday)
                .generatedTodayCount(todayCount)
                .dailyLimit(dailyLimit)
                .build();
    }
}
