package academy.cashbackmerge.config;

import java.math.BigDecimal;
import java.util.List;

public final class CampaignValidator {
    private static final int BOARD_SIZE = 4;

    private CampaignValidator() {}

    public static void validate(int boardSize, int movesLimit, List<SpawnRule> rules) {
        if (boardSize != BOARD_SIZE) {
            throw new ConfigValidationException("boardSize должен быть равен 4");
        }
        if (movesLimit <= 0) {
            throw new ConfigValidationException("movesLimit должен быть положительным");
        }
        validateSpawnRules(rules);
    }

    private static void validateSpawnRules(List<SpawnRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new ConfigValidationException("spawnRules не должен быть пустым");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (SpawnRule rule : rules) {
            if (rule == null) {
                throw new ConfigValidationException("spawnRules содержит пустое правило");
            }
            sum = sum.add(rule.probability());
        }
        if (sum.compareTo(BigDecimal.ONE) != 0) {
            throw new ConfigValidationException("Сумма вероятностей spawnRules должна быть равна 1");
        }
    }
}
