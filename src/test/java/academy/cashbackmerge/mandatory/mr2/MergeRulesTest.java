package academy.cashbackmerge.mandatory.mr2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.ShiftResult;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Обязательные тесты: правила слияния плиток. */
@DisplayName("MR2. Слияние плиток")
class MergeRulesTest {
    private static final int SIZE = 4;
    private final academy.cashbackmerge.game.GameEngine engine =
            new academy.cashbackmerge.game.GameEngine(config(), 42L);

    @ParameterizedTest
    @CsvSource({
        "'1,1,.,.', '2,.,.,.'",
        "'1,1,1,.', '2,1,.,.'",
        "'1,1,1,1', '2,2,.,.'",
        "'2,2,4,4', '4,8,.,.'",
    })
    @DisplayName("Сдвиг влево: {0} -> {1}")
    void rowShiftsLeftAndMerges(String before, String expected) {
        Board board = row(before);
        ShiftResult result = engine.previewMove(board, Direction.LEFT);
        assertTrue(result.changed());
        assertRow(result.board(), expected);
        assertRow(board, before);
    }

    @Test
    @DisplayName("Одна плитка не сливается дважды за ход: [1,1,2,.] -> [2,2,.,.]")
    void tileDoesNotMergeTwiceInOneMove() {
        ShiftResult result = engine.previewMove(row("1,1,2,."), Direction.LEFT);
        assertRow(result.board(), "2,2,.,.");
        assertEquals(2, result.scoreGained());
    }

    @Test
    @DisplayName("Слияние работает во всех четырёх направлениях")
    void mergeWorksInAllDirections() {
        for (Direction direction : Direction.values()) {
            Board board = new Board();
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                board.setTile(new Position(0, 0), tile(1));
                board.setTile(new Position(0, 1), tile(1));
            } else {
                board.setTile(new Position(0, 0), tile(1));
                board.setTile(new Position(1, 0), tile(1));
            }
            ShiftResult shift = engine.previewMove(board, direction);
            assertTrue(shift.changed());
            assertEquals(2, shift.scoreGained());
            Position destination =
                    switch (direction) {
                        case LEFT, UP -> new Position(0, 0);
                        case RIGHT -> new Position(0, SIZE - 1);
                        case DOWN -> new Position(SIZE - 1, 0);
                    };
            assertEquals(tile(2), shift.board().getTile(destination));
            assertEquals(1, count(shift.board()));
        }
    }

    private static Board row(String text) {
        Board board = new Board();
        String[] values = text.split(",");
        for (int col = 0; col < SIZE; col++) {
            if (!values[col].equals(".")) {
                board.setTile(new Position(0, col), tile(Integer.parseInt(values[col])));
            }
        }
        return board;
    }

    private static void assertRow(Board board, String text) {
        String[] values = text.split(",");
        for (int col = 0; col < SIZE; col++) {
            Tile actual = board.getTile(new Position(0, col));
            if (values[col].equals(".")) {
                assertNull(actual);
            } else {
                assertEquals(tile(Integer.parseInt(values[col])), actual);
            }
        }
    }

    private static int count(Board board) {
        return SIZE * SIZE - board.getEmptyPositions().size();
    }

    private static Tile tile(int value) {
        return new Tile(CashbackLevel.fromValue(value));
    }

    private static CampaignConfig config() {
        return new CampaignConfig(10, List.of(new SpawnRule(CashbackLevel.ONE, BigDecimal.ONE)));
    }
}
