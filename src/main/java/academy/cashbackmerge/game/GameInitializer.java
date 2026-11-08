package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class GameInitializer {

    private static final int INITIAL_TILES = 2;

    public GameState initialize(CampaignConfig config, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("Конфигурация не указана");
        }

        Random random = new Random(seed);
        Board board = new Board();

        for (int i = 0; i < INITIAL_TILES; i++) {
            spawnTile(board, config.getSpawnRules(), random);
        }

        return new GameState(board, config.getMovesLimit());
    }

    private void spawnTile(Board board, List<SpawnRule> rules, Random random) {
        List<Position> emptyPositions = board.getEmptyPositions();

        if (emptyPositions.isEmpty()) {
            throw new IllegalStateException("На поле нет свободных клеток");
        }

        int index = random.nextInt(emptyPositions.size());
        Position position = emptyPositions.get(index);

        CashbackLevel level = chooseLevel(rules, random);
        board.setTile(position, new Tile(level));
    }

    private CashbackLevel chooseLevel(List<SpawnRule> rules, Random random) {
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble());
        BigDecimal cumulative = BigDecimal.ZERO;

        for (SpawnRule rule : rules) {
            cumulative = cumulative.add(rule.probability());

            if (randomValue.compareTo(cumulative) < 0) {
                return rule.cashback();
            }
        }

        return rules.get(rules.size() - 1).cashback();
    }
}
