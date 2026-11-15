package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
    private static final int BOARD_SIZE = 4;
    private final ObjectMapper mapper = new ObjectMapper();

    public CampaignConfig load(String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new ConfigValidationException("Путь к конфигурации не указан");
        }
        CampaignJson json;
        try {
            json = mapper.readValue(new File(path), CampaignJson.class);
        } catch (JsonProcessingException e) {
            throw new ConfigLoadException("Невозможно прочитать JSON: " + path, e);
        } catch (IOException e) {
            throw new ConfigLoadException("Не удалось открыть конфигурацию: " + path, e);
        }
        return fromJson(json);
    }

    private CampaignConfig fromJson(CampaignJson json) {
        if (json == null) {
            throw new ConfigValidationException("JSON конфигурации пуст");
        }
        if (json.boardSize() == null || json.boardSize() != BOARD_SIZE) {
            throw new ConfigValidationException("boardSize должен быть равен 4");
        }
        if (json.movesLimit() == null) {
            throw new ConfigValidationException("Отсутствует movesLimit");
        }
        List<SpawnRule> rules = readRules(json.spawnRules());
        List<RewardTier> rewards = readRewards(json.rewardTable());
        CashbackLevel max = readLevel(json.maxRewardCashback(), "maxRewardCashback");
        BigDecimal rate = readDecimal(json.maxRewardAllowedRate(), "maxRewardAllowedRate");
        return new CampaignConfig(json.movesLimit(), rules, rewards, max, rate);
    }

    private List<SpawnRule> readRules(List<CampaignJson.SpawnRuleJson> source) {
        if (source == null || source.isEmpty()) {
            throw new ConfigValidationException("Отсутствуют spawnRules");
        }
        List<SpawnRule> result = new ArrayList<>();
        for (CampaignJson.SpawnRuleJson item : source) {
            if (item == null) {
                throw new ConfigValidationException("spawnRules содержит null");
            }
            result.add(new SpawnRule(
                    readLevel(item.cashback(), "spawnRules.cashback"),
                    readDecimal(item.probability(), "spawnRules.probability")));
        }
        return result;
    }

    private List<RewardTier> readRewards(List<CampaignJson.RewardJson> source) {
        if (source == null || source.isEmpty()) {
            throw new ConfigValidationException("Отсутствует rewardTable");
        }
        List<RewardTier> result = new ArrayList<>();
        for (CampaignJson.RewardJson item : source) {
            if (item == null) {
                throw new ConfigValidationException("rewardTable содержит null");
            }
            result.add(new RewardTier(
                    readLevel(item.cashback(), "rewardTable.cashback"),
                    readDecimal(item.reward(), "rewardTable.reward")));
        }
        return result;
    }

    private CashbackLevel readLevel(String text, String field) {
        if (text == null) {
            throw new ConfigValidationException("Отсутствует поле " + field);
        }
        try {
            return CashbackLevel.fromValue(Integer.parseInt(text));
        } catch (IllegalArgumentException e) {
            throw new ConfigValidationException("Некорректное поле " + field, e);
        }
    }

    private BigDecimal readDecimal(String text, String field) {
        if (text == null) {
            throw new ConfigValidationException("Отсутствует поле " + field);
        }
        try {
            return new BigDecimal(text);
        } catch (IllegalArgumentException e) {
            throw new ConfigValidationException("Некорректное поле " + field, e);
        }
    }
}
