package academy.cashbackmerge.simulation;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class StrategyComparison {
    private StrategyComparison() {}

    public static String toMarkdown(SimulationReport random, SimulationReport greedy) {
        if (random.players() != greedy.players()
                || random.seed() != greedy.seed()
                || random.movesLimit() != greedy.movesLimit()) {
            throw new IllegalArgumentException("Для сравнения нужны одинаковые параметры симуляции");
        }
        StringBuilder out = new StringBuilder();
        out.append("# Сравнение стратегий Cashback Merge\n\n");
        out.append("- Игроков для каждой стратегии: ").append(random.players()).append('\n');
        out.append("- Лимит ходов: ").append(random.movesLimit()).append('\n');
        out.append("- Seed: ").append(random.seed()).append("\n\n");
        out.append("Одинаковый seed создаёт одинаковую последовательность seed отдельных игроков. ")
                .append("Выбор ходов и последующие состояния могут отличаться.\n\n");
        out.append("| Показатель | random | greedy |\n|---|---:|---:|\n");
        metric(out, "Средний score", random.averageScore(), greedy.averageScore());
        metric(out, "Максимальный score", random.maxScore(), greedy.maxScore());
        metric(out, "Доля максимальной награды", random.actualMaxRewardRate(), greedy.actualMaxRewardRate());
        metric(out, "Допустимая доля", random.maxRewardAllowedRate(), greedy.maxRewardAllowedRate());
        metric(out, "Бизнес-статус", random.status(), greedy.status());
        distribution(out, "Максимальные плитки (%)", random.maxTileDistribution(), greedy.maxTileDistribution());
        distribution(out, "Награды", random.rewardDistribution(), greedy.rewardDistribution());
        return out.toString();
    }

    private static void metric(StringBuilder out, String name, Object random, Object greedy) {
        out.append("| ")
                .append(name)
                .append(" | ")
                .append(random)
                .append(" | ")
                .append(greedy)
                .append(" |\n");
    }

    private static <T extends Comparable<? super T>> void distribution(
            StringBuilder out, String label, Map<T, Integer> random, Map<T, Integer> greedy) {
        out.append("\n## ").append(label).append("\n\n");
        out.append("| Значение | random | greedy |\n|---|---:|---:|\n");
        Set<T> keys = new TreeSet<>();
        keys.addAll(random.keySet());
        keys.addAll(greedy.keySet());
        for (T value : keys) {
            String display = value instanceof BigDecimal decimal ? decimal.toPlainString() : value.toString();
            out.append("| ")
                    .append(display)
                    .append(" | ")
                    .append(random.getOrDefault(value, 0))
                    .append(" | ")
                    .append(greedy.getOrDefault(value, 0))
                    .append(" |\n");
        }
    }
}
