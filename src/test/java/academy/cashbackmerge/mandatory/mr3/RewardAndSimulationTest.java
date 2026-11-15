package academy.cashbackmerge.mandatory.mr3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.RewardTier;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.TileSpawner;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.simulation.RewardCalculator;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.Simulator;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: награды, симуляция и отчёт. */
@DisplayName("MR3. Награды и симуляция")
class RewardAndSimulationTest {

    private static final int PLAYERS = 30;

    private CampaignConfig config(BigDecimal allowedRate) {
        return new CampaignConfig(
                20,
                List.of(
                        new SpawnRule(CashbackLevel.ONE, new BigDecimal("0.85")),
                        new SpawnRule(CashbackLevel.TWO, new BigDecimal("0.15"))),
                List.of(
                        new RewardTier(CashbackLevel.TWO, BigDecimal.ZERO),
                        new RewardTier(CashbackLevel.FOUR, new BigDecimal("50")),
                        new RewardTier(CashbackLevel.EIGHT, new BigDecimal("150")),
                        new RewardTier(CashbackLevel.SIXTEEN, new BigDecimal("500")),
                        new RewardTier(CashbackLevel.THIRTY_TWO, new BigDecimal("2000"))),
                CashbackLevel.THIRTY_TWO,
                allowedRate);
    }

    @Test
    @DisplayName("Максимальная плитка 16% даёт награду соответствующего tier")
    void rewardMatchesHighestTile() {
        assertEquals(
                0,
                RewardCalculator.rewardFor(config(BigDecimal.ONE), CashbackLevel.SIXTEEN)
                        .compareTo(new BigDecimal("500")));
    }

    @Test
    @DisplayName("Weighted random учитывает вероятности из конфига")
    void spawnRespectsConfiguredProbabilities() {
        CampaignConfig onlyTwo = new CampaignConfig(20, List.of(new SpawnRule(CashbackLevel.TWO, BigDecimal.ONE)));
        TileSpawner spawner = new TileSpawner(onlyTwo, 42L);
        for (int i = 0; i < 50; i++) {
            Board board = new Board();
            Position position = spawner.spawn(board);
            assertEquals(CashbackLevel.TWO, board.getTile(position).level());
        }
    }

    @Test
    @DisplayName("Отчёт содержит распределение максимальных плиток и наград")
    void reportContainsDistributions() {
        SimulationReport report = new Simulator().simulate(config(BigDecimal.ONE), PLAYERS, "random", 42L);
        int tiles = report.maxTileDistribution().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        int rewards = report.rewardDistribution().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        assertEquals(PLAYERS, tiles);
        assertEquals(PLAYERS, rewards);
        assertTrue(report.toMarkdown().contains("Максимальные плитки"));
        assertTrue(report.toMarkdown().contains("Награды"));
    }

    @Test
    @DisplayName("Отчёт содержит долю игроков с максимальной наградой")
    void reportContainsMaxRewardRate() {
        SimulationReport report = new Simulator().simulate(config(BigDecimal.ONE), PLAYERS, "random", 42L);
        assertTrue(report.actualMaxRewardRate().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(report.actualMaxRewardRate().compareTo(BigDecimal.ONE) <= 0);
        assertTrue(report.toMarkdown().contains("Доля максимальной награды"));
    }

    @Test
    @DisplayName("Статус OK, если actualMaxRewardRate <= maxRewardAllowedRate, иначе FAILED")
    void reportStatusReflectsBusinessConstraint() {
        SimulationReport baseline = new Simulator().simulate(config(BigDecimal.ONE), PLAYERS, "random", 42L);
        assertEquals("OK", baseline.status());
        BigDecimal actual = baseline.actualMaxRewardRate();
        if (actual.signum() > 0) {
            SimulationReport rejected = new Simulator().simulate(config(BigDecimal.ZERO), PLAYERS, "random", 42L);
            assertEquals("FAILED", rejected.status());
        }
    }
}
