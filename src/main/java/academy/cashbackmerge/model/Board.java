package academy.cashbackmerge.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int SIZE = 4;

    private final Tile[][] cells = new Tile[SIZE][SIZE];

    public Tile getTile(Position position) {
        return cells[position.row()][position.col()];
    }

    public void setTile(Position position, Tile tile) {
        cells[position.row()][position.col()] = tile;
    }

    public boolean isEmpty(Position position) {
        return cells[position.row()][position.col()] == null;
    }

    public List<Position> getEmptyPositions() {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position position = new Position(row, col);
                if (isEmpty(position)) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }
}
