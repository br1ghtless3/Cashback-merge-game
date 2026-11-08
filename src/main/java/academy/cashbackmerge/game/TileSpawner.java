package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

public class TileSpawner {
    private final List<SpawnRule> rules;
    private final Random random;

    public TileSpawner(CampaignConfig config, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("Конфигурация не задана");
        }
        this.rules = config.getSpawnRules();
        this.random = new Random(seed);
    }

    public Position spawn(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Поле не задано");
        }
        List<Position> empty = board.getEmptyPositions();
        if (empty.isEmpty()) {
            throw new IllegalStateException("Нет свободной клетки для плитки");
        }
        Position position = empty.get(random.nextInt(empty.size()));
        CashbackLevel level = chooseLevel();
        board.setTile(position, new Tile(level));
        return position;
    }

    private CashbackLevel chooseLevel() {
        int scale = 0;
        for (SpawnRule rule : rules) {
            scale = Math.max(scale, rule.probability().scale());
        }
        BigInteger total = BigDecimal.ONE.movePointRight(scale).toBigIntegerExact();
        BigInteger ticket = randomBelow(total);
        BigInteger cumulative = BigInteger.ZERO;
        for (SpawnRule rule : rules) {
            BigInteger weight = rule.probability().movePointRight(scale).toBigIntegerExact();
            cumulative = cumulative.add(weight);
            if (ticket.compareTo(cumulative) < 0) {
                return rule.cashback();
            }
        }
        throw new IllegalStateException("Сумма вероятностей не равна единице");
    }

    private BigInteger randomBelow(BigInteger bound) {
        BigInteger result;
        do {
            result = new BigInteger(bound.bitLength(), random);
        } while (result.compareTo(bound) >= 0);
        return result;
    }
}
