package academy.cashbackmerge.game;

import academy.cashbackmerge.model.Board;

public record ShiftResult(Board board, boolean changed, int scoreGained) {}
