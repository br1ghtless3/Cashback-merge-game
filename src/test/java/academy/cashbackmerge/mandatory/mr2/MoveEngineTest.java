package academy.cashbackmerge.mandatory.mr2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.MoveResult;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: движок ходов — что считается ходом и что после него происходит. */
@DisplayName("MR2. Движок ходов")
class MoveEngineTest {

    private static final long SEED = 42L;
    private static final int LIMIT = 10;
    private static final int SIZE = 4;

    @Test
    @DisplayName("Невалидный ход не меняет поле, не тратит ход и не создаёт плитку")
    void invalidMoveChangesNothing() {
        Board board = new Board();
        board.setTile(new Position(0, 0), tile(1));
        GameState state = new GameState(board, LIMIT);
        MoveResult result = engine(LIMIT).applyMove(state, Direction.LEFT);
        assertFalse(result.changed());
        assertEquals(0, state.getMovesUsed());
        assertEquals(0, state.getScore());
        assertEquals(1, count(board));
    }

    @Test
    @DisplayName("После валидного хода появляется ровно одна новая плитка")
    void validMoveSpawnsExactlyOneTile() {
        Board board = new Board();
        board.setTile(new Position(0, 1), tile(1));
        GameState state = new GameState(board, LIMIT);
        MoveResult result = engine(LIMIT).applyMove(state, Direction.LEFT);
        assertTrue(result.changed());
        assertEquals(1, state.getMovesUsed());
        assertEquals(0, state.getScore());
        assertEquals(2, count(board));
        assertEquals(tile(1), board.getTile(new Position(0, 0)));
    }

    @Test
    @DisplayName("После N валидных ходов игра завершена")
    void gameEndsAfterMovesLimit() {
        Board board = new Board();
        board.setTile(new Position(0, 1), tile(1));
        GameState state = new GameState(board, 1);
        GameEngine game = engine(1);
        assertTrue(game.applyMove(state, Direction.LEFT).changed());
        assertTrue(state.isFinished());
        assertFalse(game.applyMove(state, Direction.RIGHT).changed());
        assertEquals(1, state.getMovesUsed());
    }

    @Test
    @DisplayName("Достижение плитки 32% не заканчивает игру — доигрываем до лимита ходов")
    void reachingMaxTileDoesNotEndGame() {
        Board board = new Board();
        board.setTile(new Position(0, 0), tile(16));
        board.setTile(new Position(0, 1), tile(16));
        GameState state = new GameState(board, LIMIT);
        MoveResult result = engine(LIMIT).applyMove(state, Direction.LEFT);
        assertTrue(result.changed());
        assertEquals(32, result.scoreGained());
        assertEquals(tile(32), board.getTile(new Position(0, 0)));
        assertFalse(state.isFinished());
    }

    @Test
    @DisplayName("Score считается по правилам и растёт только при слияниях")
    void scoreIsCalculatedOnMerges() {
        Board board = new Board();
        board.setTile(new Position(0, 0), tile(2));
        board.setTile(new Position(0, 1), tile(2));
        board.setTile(new Position(0, 2), tile(4));
        board.setTile(new Position(0, 3), tile(4));
        GameState state = new GameState(board, LIMIT);
        MoveResult result = engine(LIMIT).applyMove(state, Direction.LEFT);
        assertTrue(result.changed());
        assertEquals(12, result.scoreGained());
        assertEquals(12, state.getScore());
    }

    private static GameEngine engine(int limit) {
        CampaignConfig config = new CampaignConfig(limit, List.of(new SpawnRule(CashbackLevel.ONE, BigDecimal.ONE)));
        return new GameEngine(config, SEED);
    }

    private static Tile tile(int value) {
        return new Tile(CashbackLevel.fromValue(value));
    }

    private static int count(Board board) {
        return SIZE * SIZE - board.getEmptyPositions().size();
    }
}
