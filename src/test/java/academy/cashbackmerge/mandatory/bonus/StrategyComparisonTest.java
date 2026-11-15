package academy.cashbackmerge.mandatory.bonus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.Simulator;
import academy.cashbackmerge.simulation.StrategyComparison;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Бонус. Сравнение стратегий")
class StrategyComparisonTest {
    private static final int PLAYERS = 30;
    private static final long SEED = 42L;

    @Test
    @DisplayName("greedy воспроизводима по seed")
    void greedyIsDeterministic() throws IOException {
        CampaignConfig config = new ConfigLoader().load("config/campaign.example.json");
        Simulator simulator = new Simulator();
        SimulationReport first = simulator.simulate(config, PLAYERS, "greedy", SEED);
        SimulationReport second = simulator.simulate(config, PLAYERS, "greedy", SEED);
        assertEquals(first, second);
        assertEquals(
                PLAYERS,
                first.maxTileDistribution().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum());
    }

    @Test
    @DisplayName("сравнение содержит общие показатели обеих стратегий")
    void compareUsesSameSeed() throws IOException {
        CampaignConfig config = new ConfigLoader().load("config/campaign.example.json");
        Simulator simulator = new Simulator();
        SimulationReport random = simulator.simulate(config, PLAYERS, "random", SEED);
        SimulationReport greedy = simulator.simulate(config, PLAYERS, "greedy", SEED);
        String markdown = StrategyComparison.toMarkdown(random, greedy);
        assertTrue(markdown.contains("| Показатель | random | greedy |"));
        assertTrue(markdown.contains("Средний score"));
        assertTrue(markdown.contains("Награды"));
        assertTrue(markdown.contains("- Seed: 42"));
    }
}
