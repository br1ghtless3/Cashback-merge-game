package academy.cashbackmerge.game;

import academy.cashbackmerge.model.GameState;

public record MoveResult(GameState state, boolean changed, int scoreGained) {}
