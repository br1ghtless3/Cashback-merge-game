package academy.cashbackmerge.model;

public record Position(int row, int col) {
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 3;

    public Position {
        if (row < MIN_POSITION || row > MAX_POSITION || col < MIN_POSITION || col > MAX_POSITION) {
            throw new IllegalArgumentException("Введены некорректные координаты");
        }
    }
}
