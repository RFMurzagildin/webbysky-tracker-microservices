package ru.webbyskytracker.aiservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.aiservice.entity.read.DailyMetricReadEntity;
import ru.webbyskytracker.aiservice.entity.read.HabitCompletionReadEntity;
import ru.webbyskytracker.aiservice.entity.read.HabitReadEntity;
import ru.webbyskytracker.aiservice.repository.read.DailyMetricReadRepository;
import ru.webbyskytracker.aiservice.repository.read.HabitCompletionReadRepository;
import ru.webbyskytracker.aiservice.repository.read.HabitReadRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class ContextBuilderService {

    private final HabitReadRepository          habitRepo;
    private final HabitCompletionReadRepository completionRepo;
    private final DailyMetricReadRepository    metricRepo;

    @Value("${ai.recommendation.context-days:14}")
    private int contextDays;


    public String systemPrompt() {
        return """
                Ты — краткий и конкретный персональный коуч. Анализируй данные и давай советы.

                СТРОГИЕ ПРАВИЛА:
                - Максимум 250 слов на весь ответ
                - Никакого пересказа данных пользователя обратно — он их и так знает
                - Никаких вводных фраз типа "Хорошо, давайте посмотрим..." или "Итак, анализируя..."
                - Только конкретные действия, никакой воды
                - Пиши только на русском языке

                ФОРМАТ (строго):

                ### 💡 Общая оценка
                [1–2 предложения, только факты из данных]

                ### 🎯 Рекомендации по привычкам
                [По одной строке на каждую привычку: имя — конкретный совет]

                ### ⚡ Топ-3 действия на эту неделю
                1. [действие — одно предложение]
                2. [действие — одно предложение]
                3. [действие — одно предложение]
                """;
    }


    public String buildUserPrompt(Long userId) {
        LocalDate from = LocalDate.now().minusDays(contextDays - 1L);

        List<HabitReadEntity>           habits      = habitRepo.findByUserId(userId);
        List<Long>                      habitIds    = habits.stream().map(HabitReadEntity::getId).toList();
        List<HabitCompletionReadEntity> completions = habitIds.isEmpty() ? List.of()
                : completionRepo.findByHabitIdInAndCompletedAtGreaterThanEqual(habitIds, from);
        List<DailyMetricReadEntity>     metrics     = metricRepo
                .findByUserIdAndDateGreaterThanEqualOrderByDateDesc(userId, from);

        StringBuilder sb = new StringBuilder();
        sb.append("Проанализируй мои данные за последние ").append(contextDays).append(" дней:\n\n");

        if (!metrics.isEmpty()) {
            sb.append("=== ЕЖЕДНЕВНЫЕ МЕТРИКИ (").append(metrics.size()).append(" дней) ===\n");

            avgDouble(metrics.stream().filter(m -> m.getSleepHours() != null)
                    .mapToDouble(m -> m.getSleepHours().doubleValue()))
                    .ifPresent(v -> sb.append("• Сон: ").append(fmt1(v)).append(" ч\n"));

            avgInt(metrics.stream().filter(m -> nonZero(m.getMood()))
                    .mapToInt(DailyMetricReadEntity::getMood))
                    .ifPresent(v -> sb.append("• Настроение: ").append(fmt1(v)).append("/10\n"));

            avgInt(metrics.stream().filter(m -> nonZero(m.getProductivity()))
                    .mapToInt(DailyMetricReadEntity::getProductivity))
                    .ifPresent(v -> sb.append("• Продуктивность: ").append(fmt1(v)).append("/10\n"));

            avgInt(metrics.stream().filter(m -> nonZero(m.getEnergy()))
                    .mapToInt(DailyMetricReadEntity::getEnergy))
                    .ifPresent(v -> sb.append("• Энергия: ").append(fmt1(v)).append("/10\n"));

            avgInt(metrics.stream().filter(m -> nonZero(m.getWaterGlasses()))
                    .mapToInt(DailyMetricReadEntity::getWaterGlasses))
                    .ifPresent(v -> sb.append("• Вода: ").append(fmt1(v)).append(" стаканов/день\n"));

            avgInt(metrics.stream().filter(m -> m.getExerciseMinutes() != null && m.getExerciseMinutes() >= 0)
                    .mapToInt(DailyMetricReadEntity::getExerciseMinutes))
                    .ifPresent(v -> sb.append("• Активность: ").append(fmt1(v)).append(" мин/день\n"));

            sb.append("\n");
        } else {
            sb.append("=== ЕЖЕДНЕВНЫЕ МЕТРИКИ ===\nДанных нет.\n\n");
        }

        sb.append("=== ПРИВЫЧКИ ===\n");
        if (habits.isEmpty()) {
            sb.append("Привычек нет.\n");
        } else {
            for (HabitReadEntity h : habits) {
                long done = completions.stream()
                        .filter(c -> c.getHabitId().equals(h.getId())).count();
                int pct = (int) Math.round(done * 100.0 / contextDays);
                sb.append("• ").append(h.getName())
                  .append(": ").append(done).append("/").append(contextDays)
                  .append(" дней (").append(pct).append("%)\n");
            }
        }

        return sb.toString();
    }

    private OptionalDouble avgDouble(java.util.stream.DoubleStream s) { return s.average(); }
    private OptionalDouble avgInt   (java.util.stream.IntStream    s) { return s.average(); }
    private boolean        nonZero  (Integer v) { return v != null && v > 0; }
    private String         fmt1     (double  v) { return String.format("%.1f", v); }
}
