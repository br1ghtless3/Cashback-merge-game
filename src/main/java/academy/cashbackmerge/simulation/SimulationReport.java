package academy.cashbackmerge.simulation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public record SimulationReport(
        int players,
        int movesLimit,
        long seed,
        String strategy,
        Map<Integer, Integer> maxTileDistribution,
        Map<BigDecimal, Integer> rewardDistribution,
        BigDecimal averageScore,
        int maxScore,
        BigDecimal actualMaxRewardRate,
        BigDecimal maxRewardAllowedRate,
        String status) {

    public SimulationReport {
        maxTileDistribution = Collections.unmodifiableMap(new TreeMap<>(maxTileDistribution));
        rewardDistribution = Collections.unmodifiableMap(new TreeMap<>(rewardDistribution));
    }

    public String toMarkdown() {
        StringBuilder out = new StringBuilder();
        out.append("# Отчёт Cashback Merge\n\n");
        out.append("- Игроков: ").append(players).append('\n');
        out.append("- Лимит ходов: ").append(movesLimit).append('\n');
        out.append("- Seed: ").append(seed).append('\n');
        out.append("- Стратегия: ").append(strategy).append('\n');
        out.append("- Средний score: ").append(averageScore.toPlainString()).append('\n');
        out.append("- Максимальный score: ").append(maxScore).append('\n');
        out.append("- Доля максимальной награды: ")
                .append(actualMaxRewardRate.toPlainString())
                .append('\n');
        out.append("- Допустимая доля: ")
                .append(maxRewardAllowedRate.toPlainString())
                .append('\n');
        out.append("- Статус: **").append(status).append("**\n\n");
        out.append("## Максимальные плитки\n\n| Кэшбэк | Игроков |\n|---|---:|\n");
        maxTileDistribution.forEach((level, count) ->
                out.append("| ").append(level).append("% | ").append(count).append(" |\n"));
        out.append("\n## Награды\n\n| Награда | Игроков |\n|---|---:|\n");
        rewardDistribution.forEach((reward, count) -> out.append("| ")
                .append(reward.toPlainString())
                .append(" | ")
                .append(count)
                .append(" |\n"));
        return out.toString();
    }
}
