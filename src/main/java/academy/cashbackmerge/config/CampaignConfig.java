package academy.cashbackmerge.config;

import java.util.List;

public class CampaignConfig {
    private static final int BOARD_SIZE = 4;
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;

    public CampaignConfig(int movesLimit, List<SpawnRule> spawnRules) {
        CampaignValidator.validate(BOARD_SIZE, movesLimit, spawnRules);
        this.movesLimit = movesLimit;
        this.spawnRules = List.copyOf(spawnRules);
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public List<SpawnRule> getSpawnRules() {
        return spawnRules;
    }
}
