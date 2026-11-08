package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;

public record SpawnRule(CashbackLevel cashback, BigDecimal probability) {
    public SpawnRule {
        if (cashback == null
                || probability == null
                || probability.compareTo(BigDecimal.ZERO) <= 0
                || probability.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Ошибка данных");
        }
    }
}
