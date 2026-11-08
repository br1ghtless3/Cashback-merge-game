package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameEngine {
    private static final int SIZE = 4;
    private final TileSpawner spawner;

    public GameEngine(CampaignConfig config, long seed) {
        this.spawner = new TileSpawner(config, seed);
    }

    public MoveResult applyMove(GameState state, Direction direction) {
        if (state == null || direction == null) {
            throw new IllegalArgumentException("Не указано состояние или направление");
        }
        if (state.isFinished()) {
            return new MoveResult(state, false, 0);
        }

        ShiftResult shift = previewMove(state.getBoard(), direction);
        if (!shift.changed()) {
            return new MoveResult(state, false, 0);
        }

        Board target = state.getBoard();
        Board result = shift.board();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position position = new Position(row, col);
                target.setTile(position, result.getTile(position));
            }
        }

        state.registerMove(shift.scoreGained());
        spawner.spawn(target);
        return new MoveResult(state, true, shift.scoreGained());
    }

    public ShiftResult previewMove(Board board, Direction direction) {
        if (board == null || direction == null) {
            throw new IllegalArgumentException("Не указано поле или направление");
        }

        Board result = new Board();
        int gained = 0;
        boolean changed = false;

        for (int line = 0; line < SIZE; line++) {
            List<Tile> source = new ArrayList<>();
            for (int index = 0; index < SIZE; index++) {
                Tile tile = board.getTile(positionFor(direction, line, index));
                if (tile != null) {
                    source.add(tile);
                }
            }

            LineResult merged = mergeLine(source);
            gained += merged.scoreGained();
            for (int index = 0; index < SIZE; index++) {
                Position position = positionFor(direction, line, index);
                Tile tile = merged.tiles()[index];
                result.setTile(position, tile);
                if (!Objects.equals(board.getTile(position), tile)) {
                    changed = true;
                }
            }
        }
        return new ShiftResult(result, changed, gained);
    }

    private LineResult mergeLine(List<Tile> source) {
        Tile[] result = new Tile[SIZE];
        int count = 0;
        int gained = 0;
        int index = 0;

        while (index < source.size()) {
            Tile current = source.get(index);

            if (canMerge(source, index)) {
                int doubled = current.level().getValue() * 2;
                result[count++] = new Tile(CashbackLevel.fromValue(doubled));
                gained += doubled;
                index += 2;
            } else {
                result[count++] = current;
                index++;
            }
        }

        return new LineResult(result, gained);
    }

    private boolean canMerge(List<Tile> source, int index) {
        if (index + 1 >= source.size()) {
            return false;
        }
        Tile current = source.get(index);
        return current.level() != CashbackLevel.THIRTY_TWO
                && current.level() == source.get(index + 1).level();
    }

    private Position positionFor(Direction direction, int line, int index) {
        return switch (direction) {
            case LEFT -> new Position(line, index);
            case RIGHT -> new Position(line, SIZE - 1 - index);
            case UP -> new Position(index, line);
            case DOWN -> new Position(SIZE - 1 - index, line);
        };
    }

    private record LineResult(Tile[] tiles, int scoreGained) {}
}
