package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.CashbackLevel;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Position;
import academy.cashbackmerge.model.Tile;
import academy.cashbackmerge.simulation.RewardCalculator;
import academy.cashbackmerge.view.BoardPrinter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/** Интерактивная консольная партия. */
public final class ConsoleGame {
    private static final int SIZE = 4;
    private static final long ENGINE_SEED_MASK = 0x5DEECE66DL;

    private final Scanner input;
    private final PrintStream output;
    private final BoardPrinter printer;

    public ConsoleGame() {
        this(new Scanner(System.in, StandardCharsets.UTF_8), System.out);
    }

    public ConsoleGame(Scanner input, PrintStream output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException("Не заданы ввод или вывод");
        }
        this.input = input;
        this.output = output;
        this.printer = new BoardPrinter();
    }

    public void play(CampaignConfig config, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("Конфигурация не задана");
        }
        GameState state = new GameInitializer().initialize(config, seed);
        GameEngine engine = new GameEngine(config, seed ^ ENGINE_SEED_MASK);
        CashbackLevel highest = highestTile(state.getBoard());

        output.println("      CASHBACK MERGE");
        output.println("Управление: W/A/S/D или UP/LEFT/DOWN/RIGHT; Q — выход");
        show(state, config, highest);

        while (!state.isFinished()) {
            if (!hasValidMove(engine, state.getBoard())) {
                output.println("Доступных ходов больше нет. Партия завершена.");
                break;
            }
            output.print("Ваш ход: ");
            output.flush();
            if (!input.hasNextLine()) {
                output.println("Ввод завершён. Выход из игры.");
                return;
            }
            String command = input.nextLine().trim().toUpperCase(Locale.ROOT);
            if ("Q".equals(command) || "QUIT".equals(command) || "EXIT".equals(command)) {
                output.println("Выход из игры.");
                return;
            }
            Optional<Direction> direction = parseDirection(command);
            if (direction.isEmpty()) {
                output.println("Неизвестная команда. Используй W/A/S/D или Q.");
                continue;
            }
            MoveResult result = engine.applyMove(state, direction.orElseThrow());
            if (!result.changed()) {
                output.println("Поле не изменилось — ход не потрачен.");
                continue;
            }
            CashbackLevel now = highestTile(state.getBoard());
            if (now.getValue() > highest.getValue()) {
                highest = now;
            }
            output.println("Очки за ход: " + result.scoreGained());
            show(state, config, highest);
        }
        output.println("Игра окончена. Итоговые очки: " + state.getScore());
        output.println("Итоговая награда: "
                + RewardCalculator.rewardFor(config, highest).toPlainString());
    }

    private void show(GameState state, CampaignConfig config, CashbackLevel highest) {
        printer.print(state.getBoard());
        output.println("Осталось ходов: " + (state.getMovesLimit() - state.getMovesUsed()));
        output.println("Счёт: " + state.getScore());
        output.println("Максимальная плитка: " + highest.getValue() + "%");
        output.println("Текущая награда: "
                + RewardCalculator.rewardFor(config, highest).toPlainString());
    }

    private boolean hasValidMove(GameEngine engine, Board board) {
        for (Direction direction : Direction.values()) {
            if (engine.previewMove(board, direction).changed()) {
                return true;
            }
        }
        return false;
    }

    private CashbackLevel highestTile(Board board) {
        CashbackLevel highest = CashbackLevel.ONE;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = board.getTile(new Position(row, col));
                if (tile != null && tile.level().getValue() > highest.getValue()) {
                    highest = tile.level();
                }
            }
        }
        return highest;
    }

    private Optional<Direction> parseDirection(String command) {
        return switch (command) {
            case "W", "UP" -> Optional.of(Direction.UP);
            case "S", "DOWN" -> Optional.of(Direction.DOWN);
            case "A", "LEFT" -> Optional.of(Direction.LEFT);
            case "D", "RIGHT" -> Optional.of(Direction.RIGHT);
            default -> Optional.empty();
        };
    }
}
