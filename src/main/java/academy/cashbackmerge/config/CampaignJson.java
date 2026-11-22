package academy.cashbackmerge.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CampaignJson(Integer boardSize, Integer movesLimit, List<SpawnRuleJson> spawnRules) {
    @JsonCreator
    public CampaignJson(
            @JsonProperty("boardSize") Integer boardSize,
            @JsonProperty("movesLimit") Integer movesLimit,
            @JsonProperty("spawnRules") List<SpawnRuleJson> spawnRules) {
        this.boardSize = boardSize;
        this.movesLimit = movesLimit;
        this.spawnRules = spawnRules;
    }

    public record SpawnRuleJson(String cashback, String probability) {}
}
