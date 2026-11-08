package academy.cashbackmerge.model;

public record Tile(CashbackLevel level) {
    public Tile {
        if (level == null) {
            throw new IllegalArgumentException("Некорректное значение плитки");
        }
    }
}
