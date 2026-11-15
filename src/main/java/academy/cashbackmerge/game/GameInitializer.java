package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.GameState;

public class GameInitializer {
    private static final int INITIAL_TILES = 2;

    public GameState initialize(CampaignConfig config, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("Конфигурация не указана");
        }
        Board board = new Board();
        TileSpawner spawner = new TileSpawner(config, seed);
        for (int i = 0; i < INITIAL_TILES; i++) {
            spawner.spawn(board);
        }
        return new GameState(board, config.getMovesLimit());
    }
}
