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
     * Displays the main menu and processes user selections.
     *
     * <p>The menu remains active until the user chooses to continue,
     * successfully loads a saved game, or the input stream reaches EOF.
     * Available actions include saving, loading, deleting saves, viewing
     * the Hall of Fame, and exiting the application.</p>
     *
     * @param canSave whether the current game can be saved
     * @param data the current game data to save
     * @return the loaded save data, or {@code null} when the user
     *         continues without loading a save or input reaches EOF
     */
    public T show(boolean canSave, T data) {

        while (true) {

            printer.printMainMenu();

            String userChoice = input.userInput();

            if (userChoice == null) { return null; }

            MenuAction action = getMenuAction(userChoice.trim());

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

    /**
     * Converts a menu selection into its corresponding action.
     *
     * @param userChoice the trimmed user input representing a menu option
     * @return the matching menu action, or {@link MenuAction#INVALID}
     *         if the input does not match a recognised option
     */
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

    /**
     * Prompts the user for a save name and saves the current game.
     *
     * <p>If the supplied name is invalid, an error message is displayed
     * and the user is prompted again. If the input stream reaches EOF,
     * the method returns without saving.</p>
     *
     * @param data the current game data to save
     */
    private void saveCurrentGame(T data) {

        while (true) {
            printer.newLine();
            printer.printMessage("Enter save name:");

            String saveName = input.userInput();

            if (saveName == null) {
                return;
            }

            saveName = saveName.trim();

            try {
                if (saveGame.saveExists(saveName)) {
                    printer.printMessage("Name already taken. Please choose another name.");
                    continue;
                }

                saveGame.save(saveName, data);
                printer.printMessage("Game saved successfully.");
                return;

            } catch (IllegalArgumentException e) {
                printer.printMessage(
                        "Invalid save name. Use only letters, numbers, spaces, underscores, and hyphens."
                );
            }
        }
    }

    /**
     * Displays the available saves and loads the user's selection.
     *
     * <p>Invalid selections are rejected with an error message.
     * If the input stream reaches EOF, the method returns {@code null}.</p>
     *
     * @return the loaded game data, or {@code null} if the selection
     *         is invalid, no save is loaded, or input reaches EOF
     */
    private T loadGame() {

        printer.newLine();

        File[] saves;

        try {
            saves = saveGame.getSaveFiles();
        } catch (IllegalStateException e) {
            printer.printMessage("Unable to access save files. Please check the save directory.");
            return null;
        }
        printer.printGameSaves(saveGame.getSaveFiles());

        String userChoice = input.userInput();

        if (userChoice == null) { return null; }

        userChoice = userChoice.trim();

        int selection;

        try {
            selection = Integer.parseInt(userChoice);
        } catch (NumberFormatException e) {
            printer.printMessage("Invalid save selection.");
            return null;
        }

        if (selection < 1 || selection > saves.length) {
            printer.printMessage("Invalid save selection.");
            return null;
        }

        String saveName = saveGame.getSaveName(saves, selection);

        if (!saveGame.saveExists(saveName)) {

            printer.printMessage("Invalid save selection.");
            return null;
        }

        T saveData = saveGame.load(saveName);

        printer.printMessage(
                "Save loaded successfully."
        );
        return saveData;
    }

    /**
     * Displays the available saves and deletes the selected save.
     *
     * <p>If the selection is not a valid number, an error message is
     * displayed and no save is deleted.</p>
     */
    private void deleteGame() {

        printer.newLine();

        File[] saves;

        try {
            saves = saveGame.getSaveFiles();
        } catch (IllegalStateException e) {
            printer.printMessage("Unable to access save files. Please check the save directory.");
            return;
        }
        printer.printGameSaves(saveGame.getSaveFiles());

        String userChoice = input.userInput();

        if (userChoice == null) { return; }

        userChoice = userChoice.trim();

        int selection;

        try {
            selection = Integer.parseInt(userChoice);
        } catch (NumberFormatException e) {
            printer.printMessage("Invalid save selection.");
            return;
        }

        if (selection < 1 || selection > saves.length) {
            printer.printMessage("Invalid save selection.");
            return;
        }

        String saveName = saveGame.getSaveName(saves, selection);

        if (saveGame.saveExists(saveName)) {

            saveGame.deleteSave(saveName);

            printer.printMessage("Save deleted successfully.");

        } else {
            printer.printMessage("Save not found.");
        }
    }

    /**
     * Informs the user that there is no current game available to save.
     */
    private void noGameToSave() {
        printer.printMessage("No game to save!");
    }

    /**
     * Displays the historical results stored in the Hall of Fame.
     */
    private void viewHallOfFame() {
        printer.printPodium(hallOfFame.getHistory());
    }
}