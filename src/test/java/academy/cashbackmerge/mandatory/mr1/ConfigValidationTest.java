package academy.cashbackmerge.mandatory.mr1;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

    private List<SpawnRule> validRules() {
        return List.of(
                new SpawnRule(CashbackLevel.ONE, new BigDecimal("0.85")),
                new SpawnRule(CashbackLevel.TWO, new BigDecimal("0.15")));
    }

    @Test
    @DisplayName("Валидный конфиг принимается")
    void validConfigIsAccepted() {
        CampaignConfig config = assertDoesNotThrow(() -> new CampaignConfig(10, validRules()));

        assertEquals(10, config.getMovesLimit());
        assertEquals(2, config.getSpawnRules().size());
    }

    @Test
    @DisplayName("Сумма вероятностей spawn-правил не равна 1 — конфиг отклоняется")
    void probabilitiesNotSummingToOneAreRejected() {
        List<SpawnRule> rules = List.of(
                new SpawnRule(CashbackLevel.ONE, new BigDecimal("0.60")),
                new SpawnRule(CashbackLevel.TWO, new BigDecimal("0.30")));

        assertThrows(IllegalArgumentException.class, () -> new CampaignConfig(10, rules));
    }

    @Test
    @DisplayName("movesLimit <= 0 — конфиг отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CampaignConfig(0, validRules()));

        assertThrows(IllegalArgumentException.class, () -> new CampaignConfig(-1, validRules()));
    }

    @Test
    @DisplayName("Пустой список spawn-правил — конфиг отклоняется")
    void emptySpawnRulesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CampaignConfig(10, List.of()));
    }
}
