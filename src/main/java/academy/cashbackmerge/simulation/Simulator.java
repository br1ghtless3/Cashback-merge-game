package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameInitializer;
import academy.cashbackmerge.game.ShiftResult;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Simulator {
    private static final int BOARD_SIZE = 4;
    private static final int RATE_SCALE = 12;
    private static final int SCORE_SCALE = 3;
    private static final long ENGINE_SEED_SALT = 0x5DEECE66DL;
    private static final long STRATEGY_SEED_SALT = 0x9E3779B97F4A7C15L;

    public SimulationReport simulate(CampaignConfig config, int players, String strategy, long seed) {
        if (config == null || players <= 0 || !"random".equals(strategy)) {
            throw new IllegalArgumentException("Нужны конфигурация, players > 0 и стратегия random");
        }
        Map<Integer, Integer> maxTiles = new TreeMap<>();
        Map<BigDecimal, Integer> rewards = new TreeMap<>();
        Random seeds = new Random(seed);
        long scores = 0;
        int maxScore = 0;
        int maxRewardCount = 0;

        for (int player = 0; player < players; player++) {
            long playerSeed = seeds.nextLong();
            PlayerOutcome outcome = play(config, playerSeed);
            maxTiles.merge(outcome.highest().getValue(), 1, Integer::sum);
            BigDecimal reward = RewardCalculator.rewardFor(config, outcome.highest());
            rewards.merge(reward, 1, Integer::sum);
            scores += outcome.score();
            maxScore = Math.max(maxScore, outcome.score());
            if (outcome.highest().getValue() >= config.getMaxRewardCashback().getValue()) {
                maxRewardCount++;
            }
        }
        BigDecimal actual = BigDecimal.valueOf(maxRewardCount)
                .divide(BigDecimal.valueOf(players), RATE_SCALE, RoundingMode.HALF_UP);
        BigDecimal average =
                BigDecimal.valueOf(scores).divide(BigDecimal.valueOf(players), SCORE_SCALE, RoundingMode.HALF_UP);
        // Сравниваем исходные целые числа, не округлённую долю из отчёта.
        boolean accepted = BigDecimal.valueOf(maxRewardCount)
                        .compareTo(config.getMaxRewardAllowedRate().multiply(BigDecimal.valueOf(players)))
                <= 0;
        return new SimulationReport(
                players,
                config.getMovesLimit(),
                seed,
                strategy,
                maxTiles,
                rewards,
                average,
                maxScore,
                actual,
                config.getMaxRewardAllowedRate(),
                accepted ? "OK" : "FAILED");
    }

    private PlayerOutcome play(CampaignConfig config, long seed) {
        GameState state = new GameInitializer().initialize(config, seed);
        GameEngine engine = new GameEngine(config, seed ^ ENGINE_SEED_SALT);
        Random choices = new Random(seed ^ STRATEGY_SEED_SALT);
        CashbackLevel highest = highestTile(state.getBoard());

        while (!state.isFinished()) {
            List<Direction> valid = legalDirections(engine, state.getBoard());
            if (valid.isEmpty()) {
                break;
            }
            Direction move = valid.get(choices.nextInt(valid.size()));
            engine.applyMove(state, move);
            CashbackLevel now = highestTile(state.getBoard());
            if (now.getValue() > highest.getValue()) {
                highest = now;
            }
        }
        return new PlayerOutcome(highest, state.getScore());
    }

    private List<Direction> legalDirections(GameEngine engine, Board board) {
        List<Direction> valid = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            ShiftResult preview = engine.previewMove(board, direction);
            if (preview.changed()) {
                valid.add(direction);
            }
        }
        return valid;
    }

    private CashbackLevel highestTile(Board board) {
        CashbackLevel highest = CashbackLevel.ONE;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Tile tile = board.getTile(new Position(row, col));
                if (tile != null && tile.level().getValue() > highest.getValue()) {
                    highest = tile.level();
                }
            }
        }
        return highest;
    }

    private record PlayerOutcome(CashbackLevel highest, int score) {}
}
