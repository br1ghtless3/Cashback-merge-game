package academy.cashbackmerge.model;

public class GameState {
    private final Board board;
    private int movesUsed = 0; // кол-во исп ходов
    private final int movesLimit; // макс число ходов
    private int score = 0; // очки за объединение

    public GameState(Board board, int movesLimit) {
        if (board == null || movesLimit <= 0) {
            throw new IllegalArgumentException("Переданы некорректные значения.");
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
}
