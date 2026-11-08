package academy.cashbackmerge;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.game.GameInitializer;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.view.BoardPrinter;
import java.io.IOException;

public final class Main {

    private static final long DEFAULT_SEED = 42L;
    private static final String DEFAULT_CONFIG = "config/campaign.example.json";

    private Main() {}

    public static void main(String[] args) throws IOException {
        String configPath = DEFAULT_CONFIG;
        long seed = DEFAULT_SEED;

        if (args.length > 0) {
            configPath = args[0];
        }

        if (args.length > 1) {
            seed = Long.parseLong(args[1]);
        }

        ConfigLoader loader = new ConfigLoader();
        CampaignConfig config = loader.load(configPath);

        GameInitializer initializer = new GameInitializer();
        GameState state = initializer.initialize(config, seed);

        System.out.println("   CASHBACK MERGE");
        System.out.println("Seed: " + seed);
        System.out.println("Лимит ходов: " + state.getMovesLimit());
        System.out.println("Использовано ходов: " + state.getMovesUsed());
        System.out.println("Score: " + state.getScore());
        System.out.println();

        BoardPrinter printer = new BoardPrinter();
        printer.print(state.getBoard());
    }
}
