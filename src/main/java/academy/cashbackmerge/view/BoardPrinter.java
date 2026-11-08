package academy.cashbackmerge.view;

import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;

public class BoardPrinter {

    private static final int SIZE = 4;
    private static final String BORDER = "+------+------+------+------+";

    public void print(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Игровое поле не указано");
        }

        System.out.println(BORDER);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = board.getTile(new Position(row, col));

                if (tile == null) {
                    System.out.printf("| %4s ", "");
                } else {
                    String value = tile.level().getValue() + "%";
                    System.out.printf("| %4s ", value);
                }
            }

            System.out.println("|");
            System.out.println(BORDER);
        }
    }
}
