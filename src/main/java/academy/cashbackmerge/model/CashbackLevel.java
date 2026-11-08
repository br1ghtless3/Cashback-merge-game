package academy.cashbackmerge.model;

public enum CashbackLevel {
    ONE(1),
    TWO(2),
    FOUR(4),
    EIGHT(8),
    SIXTEEN(16),
    THIRTY_TWO(32);

    private final int value;

    CashbackLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CashbackLevel fromValue(int value) {
        for (CashbackLevel level : CashbackLevel.values()) {
            if (level.getValue() == value) {
                return level;
            }
        }

        throw new IllegalArgumentException("Недопустимый уровень кэшбэка: " + value);
    }
}
