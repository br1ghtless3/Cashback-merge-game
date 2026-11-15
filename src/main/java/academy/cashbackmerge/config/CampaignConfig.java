package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import java.math.BigDecimal;
import java.util.List;

public class CampaignConfig {
    private static final int BOARD_SIZE = 4;
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;
    private final List<RewardTier> rewardTable;
    private final CashbackLevel maxRewardCashback;
    private final BigDecimal maxRewardAllowedRate;

    public CampaignConfig(int movesLimit, List<SpawnRule> spawnRules) {
        this(movesLimit, spawnRules, List.of(), CashbackLevel.THIRTY_TWO, BigDecimal.ONE);
    }

    public CampaignConfig(
            int movesLimit,
            List<SpawnRule> spawnRules,
            List<RewardTier> rewardTable,
            CashbackLevel maxRewardCashback,
            BigDecimal maxRewardAllowedRate) {
        CampaignValidator.validate(
                BOARD_SIZE, movesLimit, spawnRules, rewardTable, maxRewardCashback, maxRewardAllowedRate);
        this.movesLimit = movesLimit;
        this.spawnRules = List.copyOf(spawnRules);
        this.rewardTable = List.copyOf(rewardTable);
        this.maxRewardCashback = maxRewardCashback;
        this.maxRewardAllowedRate = maxRewardAllowedRate;
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public List<SpawnRule> getSpawnRules() {
        return spawnRules;
    }

    public List<RewardTier> getRewardTable() {
        return rewardTable;
    }

    public CashbackLevel getMaxRewardCashback() {
        return maxRewardCashback;
    }

    public BigDecimal getMaxRewardAllowedRate() {
        return maxRewardAllowedRate;
    }
}
