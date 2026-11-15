package academy.cashbackmerge.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CampaignJson(
        Integer boardSize,
        Integer movesLimit,
        List<SpawnRuleJson> spawnRules,
        List<RewardJson> rewardTable,
        String maxRewardCashback,
        String maxRewardAllowedRate) {

    @JsonCreator
    public CampaignJson(
            @JsonProperty("boardSize") Integer boardSize,
            @JsonProperty("movesLimit") Integer movesLimit,
            @JsonProperty("spawnRules") List<SpawnRuleJson> spawnRules,
            @JsonProperty("rewardTable") List<RewardJson> rewardTable,
            @JsonProperty("maxRewardCashback") String maxRewardCashback,
            @JsonProperty("maxRewardAllowedRate") String maxRewardAllowedRate) {
        this.boardSize = boardSize;
        this.movesLimit = movesLimit;
        this.spawnRules = spawnRules;
        this.rewardTable = rewardTable;
        this.maxRewardCashback = maxRewardCashback;
        this.maxRewardAllowedRate = maxRewardAllowedRate;
    }

    public record SpawnRuleJson(String cashback, String probability) {}

    public record RewardJson(String cashback, String reward) {}
}
