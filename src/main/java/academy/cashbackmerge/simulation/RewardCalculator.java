package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.RewardTier;
import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;

public final class RewardCalculator {
    private RewardCalculator() {}

    public static BigDecimal rewardFor(CampaignConfig config, CashbackLevel highest) {
        if (config == null || highest == null) {
            throw new IllegalArgumentException("Не заданы конфигурация или уровень");
        }
        int best = 0;
        BigDecimal reward = BigDecimal.ZERO;
        for (RewardTier tier : config.getRewardTable()) {
            int level = tier.cashback().getValue();
            if (level <= highest.getValue() && level > best) {
                best = level;
                reward = tier.reward();
            }
        }
        return reward;
    }
}
