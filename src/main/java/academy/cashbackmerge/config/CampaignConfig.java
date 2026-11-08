package academy.cashbackmerge.config;

import java.math.BigDecimal;
import java.util.List;

public class CampaignConfig {
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;

    public CampaignConfig(int movesLimit, List<SpawnRule> spawnRules) {
        if (movesLimit <= 0 || spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException("Некорректные параметры конфигурации");
        }

        for (SpawnRule rule : spawnRules) {
            if (rule == null) {
                throw new IllegalArgumentException("Список содержит пустое правило");
            }
        }

        var sum = BigDecimal.ZERO;

        for (SpawnRule rule : spawnRules) {
            sum = sum.add(rule.probability());
        }

        if (sum.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("Сумма вероятностей должна быть равна 1");
        }

        this.movesLimit = movesLimit;
        this.spawnRules = List.copyOf(spawnRules);
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public List<SpawnRule> getSpawnRules() {
        return spawnRules;
    }
}
