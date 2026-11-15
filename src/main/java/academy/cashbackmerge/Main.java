package academy.cashbackmerge;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.game.ConsoleGame;
import academy.cashbackmerge.game.GameInitializer;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.Simulator;
import academy.cashbackmerge.simulation.StrategyComparison;
import academy.cashbackmerge.view.BoardPrinter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private static final String DEFAULT_CONFIG = "config/campaign.example.json";
    private static final long DEFAULT_SEED = 42L;
    private static final int DEFAULT_PLAYERS = 1000;

    private Main() {}

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && ("simulate".equals(args[0]) || "compare".equals(args[0]))) {
            simulate(args);
        } else if (args.length > 0 && "play".equals(args[0])) {
            play(args);
        } else {
            String file = args.length > 0 ? args[0] : DEFAULT_CONFIG;
            long seed = args.length > 1 ? Long.parseLong(args[1]) : DEFAULT_SEED;
            CampaignConfig config = new ConfigLoader().load(file);
            GameState state = new GameInitializer().initialize(config, seed);
            System.out.println("CASHBACK MERGE | seed=" + seed);
            System.out.println("Лимит ходов: " + state.getMovesLimit());
            new BoardPrinter().print(state.getBoard());
        }
    }

    private static void play(String[] args) throws IOException {
        String configPath = DEFAULT_CONFIG;
        long seed = DEFAULT_SEED;
        int index = 1;
        while (index < args.length) {
            String key = args[index];
            if (index + 1 >= args.length) {
                throw new IllegalArgumentException("Отсутствует значение параметра " + key);
            }
            String value = args[index + 1];
            index += 2;
            switch (key) {
                case "--config" -> configPath = value;
                case "--seed" -> seed = Long.parseLong(value);
                default -> throw new IllegalArgumentException("Неизвестный параметр: " + key);
            }
        }
        CampaignConfig config = new ConfigLoader().load(configPath);
        new ConsoleGame().play(config, seed);
    }

    private static void simulate(String[] args) throws IOException {
        String configPath = DEFAULT_CONFIG;
        int players = DEFAULT_PLAYERS;
        long seed = DEFAULT_SEED;
        String strategy = "random";
        String output = "report.md";
        int i = 1;
        while (i < args.length) {
            String key = args[i];
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Отсутствует значение параметра " + key);
            }
            String value = args[i + 1];
            i += 2;
            switch (key) {
                case "--config" -> configPath = value;
                case "--players" -> players = Integer.parseInt(value);
                case "--strategy" -> strategy = value;
                case "--seed" -> seed = Long.parseLong(value);
                case "--output" -> output = value;
                default -> throw new IllegalArgumentException("Неизвестный параметр: " + key);
            }
        }
        CampaignConfig config = new ConfigLoader().load(configPath);
        if ("compare".equals(args[0])) {
            Simulator simulator = new Simulator();
            SimulationReport random = simulator.simulate(config, players, "random", seed);
            SimulationReport greedy = simulator.simulate(config, players, "greedy", seed);
            Files.writeString(Path.of(output), StrategyComparison.toMarkdown(random, greedy));
            System.out.println("Сравнение стратегий сохранено: " + output);
            System.out.println("Random: средний score = " + random.averageScore() + ", доля максимальной награды = "
                    + random.actualMaxRewardRate());
            System.out.println("Greedy: средний score = " + greedy.averageScore() + ", доля максимальной награды = "
                    + greedy.actualMaxRewardRate());
            return;
        }
        SimulationReport report = new Simulator().simulate(config, players, strategy, seed);
        Files.writeString(Path.of(output), report.toMarkdown());
        System.out.println("Отчёт сохранён: " + output);
        System.out.println("Статус: " + report.status());
        System.out.println("Доля максимальной награды: " + report.actualMaxRewardRate());
    }
}
