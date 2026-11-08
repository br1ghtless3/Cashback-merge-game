package academy.cashbackmerge.config;

import academy.cashbackmerge.model.CashbackLevel;
import com.fasterxml.jackson.databind.JsonNode;
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
            throw new IllegalArgumentException("Путь к конфигурации не указан");
        }

        JsonNode root = mapper.readTree(new File(path));

        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Конфигурация должна быть JSON-объектом");
        }

        validateBoardSize(root);

        int movesLimit = readMovesLimit(root);
        List<SpawnRule> rules = readSpawnRules(root);

        return new CampaignConfig(movesLimit, rules);
    }

    private void validateBoardSize(JsonNode root) {
        JsonNode node = root.get("boardSize");

        if (node == null || !node.isIntegralNumber() || !node.canConvertToInt() || node.intValue() != BOARD_SIZE) {
            throw new IllegalArgumentException("Размер игрового поля должен быть 4");
        }
    }

    private int readMovesLimit(JsonNode root) {
        JsonNode node = root.get("movesLimit");

        if (node == null || !node.isIntegralNumber() || !node.canConvertToInt() || node.intValue() <= 0) {
            throw new IllegalArgumentException("Некорректный лимит ходов");
        }

        return node.intValue();
    }

    private List<SpawnRule> readSpawnRules(JsonNode root) {
        JsonNode node = root.get("spawnRules");

        if (node == null || !node.isArray() || node.isEmpty()) {
            throw new IllegalArgumentException("Список spawn-правил отсутствует или пуст");
        }

        List<SpawnRule> rules = new ArrayList<>();

        for (JsonNode ruleNode : node) {
            rules.add(readSpawnRule(ruleNode));
        }

        return rules;
    }

    private SpawnRule readSpawnRule(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("Некорректное правило появления плитки");
        }

        CashbackLevel level = readCashback(node);
        BigDecimal probability = readProbability(node);

        return new SpawnRule(level, probability);
    }

    private CashbackLevel readCashback(JsonNode node) {
        JsonNode cashbackNode = node.get("cashback");

        if (cashbackNode == null || !cashbackNode.isTextual()) {
            throw new IllegalArgumentException("Кэшбэк должен быть строкой");
        }

        try {
            int value = Integer.parseInt(cashbackNode.textValue());
            return CashbackLevel.fromValue(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение кэшбэка", e);
        }
    }

    private BigDecimal readProbability(JsonNode node) {
        JsonNode probabilityNode = node.get("probability");

        if (probabilityNode == null || !probabilityNode.isTextual()) {
            throw new IllegalArgumentException("Вероятность должна быть строкой");
        }

        try {
            return new BigDecimal(probabilityNode.textValue());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение вероятности", e);
        }
    }
}
