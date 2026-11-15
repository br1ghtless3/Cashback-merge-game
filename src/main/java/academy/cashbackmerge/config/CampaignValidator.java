package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CampaignValidator {
    private static final int BOARD_SIZE = 4;

    private CampaignValidator() {}

    public static void validate(
            int boardSize,
            int movesLimit,
            List<SpawnRule> rules,
            List<RewardTier> rewards,
            CashbackLevel maxLevel,
            BigDecimal allowedRate) {
        if (boardSize != BOARD_SIZE) {
            throw new ConfigValidationException("boardSize должен быть равен 4");
        }
        if (movesLimit <= 0) {
            throw new ConfigValidationException("movesLimit должен быть положительным");
        }
        validateSpawnRules(rules);
        if (rewards == null) {
            throw new ConfigValidationException("rewardTable отсутствует");
        }
        validateRewards(rewards);
        if (maxLevel == null) {
            throw new ConfigValidationException("maxRewardCashback отсутствует");
        }
        if (allowedRate == null || allowedRate.signum() < 0 || allowedRate.compareTo(BigDecimal.ONE) > 0) {
            throw new ConfigValidationException("maxRewardAllowedRate должен быть от 0 до 1");
        }
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

    private static void validateRewards(List<RewardTier> rewards) {
        Set<CashbackLevel> seen = new HashSet<>();
        for (RewardTier reward : rewards) {
            if (reward == null || !seen.add(reward.cashback())) {
                throw new ConfigValidationException("rewardTable содержит пустой или повторный уровень");
            }
        }
    }
}
