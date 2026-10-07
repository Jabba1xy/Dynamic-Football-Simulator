package ui;

import data.HallOfFame;
import data.SaveGame;
import domain.enums.MenuAction;

import java.io.File;

/**
 * Manages the main game menu and user interactions with save functionality.
 *
 * <p>This class handles continuing, saving, loading, deleting saved games,
 * viewing the Hall of Fame, and exiting the application. Menu presentation
 * is delegated to {@link ConsolePrinter}, while save operations are handled
 * by {@link SaveGame}.</p>
 *
 * @param <T> type of save data managed by this menu
 */
public class GameMenu<T> {

    private final ConsolePrinter printer;
    private final SaveGame<T> saveGame;
    private final HallOfFame hallOfFame;
    private final Input input;

    /**
     * Creates a game menu using the supplied presentation, save, Hall of Fame,
     * and input components.
     *
     * <p>The dependencies are supplied externally rather than created internally,
     * keeping the menu loosely coupled to its supporting components and making
     * it easier to test.</p>
     *
     * @param printer handles console output and menu presentation
     * @param saveGame handles saving, loading, and deleting game data
     * @param hallOfFame stores completed Grand Championship results
     * @param input handles console user input
     */
    public GameMenu(
            ConsolePrinter printer,
            SaveGame<T> saveGame,
            HallOfFame hallOfFame,
            Input input
    ) {
        this.printer = printer;
        this.saveGame = saveGame;
        this.hallOfFame = hallOfFame;
        this.input = input;
    }

    /**
     * Displays the main game menu and processes user selections.
     *
     * <p>The menu continues to accept input until the user chooses to continue
     * or successfully loads a saved game.</p>
     *
     * @param canSave whether saving is currently available
     * @param data current game data to save
     * @return loaded save data when a game is loaded, or {@code null} when
     * continuing without loading a save
     */
    public T show(boolean canSave, T data) {

        while (true) {

            printer.printMainMenu();

            MenuAction action = getMenuAction(
                    input.userInput().trim()
            );

            switch (action) {

                case CONTINUE -> {
                    return null;
                }
                case SAVE -> {
                    if (canSave) {
                        saveCurrentGame(data);
                    } else {
                        noGameToSave();
                    }
                }
                case LOAD -> {
                    T saveData = loadGame();

                    if (saveData != null) {
                        return saveData;
                    }
                }
                case DELETE -> deleteGame();
                case HALL_OF_FAME -> viewHallOfFame();
                case EXIT -> System.exit(0);
                case INVALID -> printer.printMessage("Invalid option!");
            }

            printer.newLine();
        }
    }

    private MenuAction getMenuAction(String userChoice) {

        return switch (userChoice) {
            case "1" -> MenuAction.CONTINUE;
            case "2" -> MenuAction.SAVE;
            case "3" -> MenuAction.LOAD;
            case "4" -> MenuAction.DELETE;
            case "5" -> MenuAction.HALL_OF_FAME;
            case "6" -> MenuAction.EXIT;
            default -> MenuAction.INVALID;
        };
    }

    private void saveCurrentGame(T data) {

        printer.newLine();
        printer.printMessage("Enter save name:");

        String saveName = input.userInput().trim();
        saveGame.save(saveName, data);

        printer.printMessage("Game saved successfully.");
    }

    private T loadGame() {

        printer.newLine();

        printer.printGameSaves(saveGame.getSaveFiles());

        int selection = Integer.parseInt(input.userInput().trim());

        File[] saves = saveGame.getSaveFiles();

        String saveName = saveGame.getSaveName(saves, selection);

        if (!saveGame.saveExists(saveName)) {

            printer.printMessage(
                    "Invalid save selection."
            );
            return null;
        }

        T saveData = saveGame.load(saveName);

        printer.printMessage(
                "Save loaded successfully."
        );
        return saveData;
    }

    private void deleteGame() {

        printer.newLine();

        printer.printGameSaves(saveGame.getSaveFiles());

        int selection = Integer.parseInt(
                input.userInput().trim()
        );

        File[] saves = saveGame.getSaveFiles();

        String saveName = saveGame.getSaveName(saves, selection);

        if (saveGame.saveExists(saveName)) {

            saveGame.deleteSave(saveName);

            printer.printMessage("Save deleted successfully.");

        } else {
            printer.printMessage("Save not found.");
        }
    }
    private void noGameToSave() {
        printer.printMessage("No game to save!");
    }
    private void viewHallOfFame() {
        printer.printPodium(hallOfFame.getHistory());
    }
}