package academy.cashbackmerge.model;

public class GameState {
    private final Board board;
    private final int movesLimit;
    private int movesUsed;
    private int score;

    public GameState(Board board, int movesLimit) {
        if (board == null || movesLimit <= 0) {
            throw new IllegalArgumentException("Некорректное состояние игры");
        }
        this.board = board;
        this.movesLimit = movesLimit;
    }

    public Board getBoard() {
        return board;
    }

    public int getMovesUsed() {
        return movesUsed;
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public int getScore() {
        return score;
    }

    public boolean isFinished() {
        return movesUsed >= movesLimit;
    }

    public void registerMove(int scoreGained) {
        if (isFinished()) {
            throw new IllegalStateException("Лимит ходов исчерпан");
        }
        if (scoreGained < 0) {
            throw new IllegalArgumentException("Очки не могут быть отрицательными");
        }
        movesUsed++;
        score += scoreGained;
    }
}
