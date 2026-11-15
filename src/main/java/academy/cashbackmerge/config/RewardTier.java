package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;

public record RewardTier(CashbackLevel cashback, BigDecimal reward) {
    public RewardTier {
        if (cashback == null || reward == null || reward.signum() < 0) {
            throw new IllegalArgumentException("Некорректный уровень награды");
        }
    }
}
