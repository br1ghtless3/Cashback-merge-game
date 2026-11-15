package academy.cashbackmerge.mandatory.mr1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.GameInitializer;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.Simulator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {
    private static final int SIZE = 4;
    private static final int MOVES_LIMIT = 10;
    private static final long SEED = 42L;

    @Test
    @DisplayName("Одинаковый конфиг и seed дают одинаковое начальное поле")
    void sameConfigAndSeedProduceSameInitialBoard() {
        CampaignConfig config = new CampaignConfig(
                MOVES_LIMIT,
                List.of(
                        new SpawnRule(CashbackLevel.ONE, new BigDecimal("0.85")),
                        new SpawnRule(CashbackLevel.TWO, new BigDecimal("0.15"))));

        GameInitializer initializer = new GameInitializer();

        GameState first = initializer.initialize(config, SEED);
        GameState second = initializer.initialize(config, SEED);

        Board firstBoard = first.getBoard();
        Board secondBoard = second.getBoard();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position position = new Position(row, col);

                assertEquals(firstBoard.getTile(position), secondBoard.getTile(position));
            }
        }

        assertEquals(first.getMovesUsed(), second.getMovesUsed());
        assertEquals(first.getScore(), second.getScore());
    }

    @Test
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() throws IOException {
        CampaignConfig config = new ConfigLoader().load("config/campaign.example.json");

        Simulator simulator = new Simulator();

        SimulationReport first = simulator.simulate(config, 50, "random", 42L);

        SimulationReport second = simulator.simulate(config, 50, "random", 42L);

        assertEquals(first, second);
    }
}
