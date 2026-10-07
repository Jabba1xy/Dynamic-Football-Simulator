package ui;

import data.HallOfFame;
import data.SaveGame;
import domain.dto.GrandChampionPodium;
import domain.enums.SimulationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMenuTest {

    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void showReturnsNullWhenUserChoosesContinue() {

        GameMenu<String> menu = createMenu(
                "1\n",
                new FakeSaveGame()
        );
        String result = menu.show(false, null);

        assertNull(result);
    }
    @Test
    void showDisplaysInvalidOptionMessage() {

        GameMenu<String> menu = createMenu(
                "invalid\n1\n",
                new FakeSaveGame()
        );

        menu.show(false, null);

        assertTrue(output.toString().contains("Invalid option!"));
    }
    @Test
    void showDisplaysNoGameToSaveWhenSavingIsUnavailable() {

        GameMenu<String> menu = createMenu(
                "2\n1\n",
                new FakeSaveGame()
        );
        menu.show(false, null);

        assertTrue(output.toString().contains("No game to save!"));
    }
    @Test
    void showSavesGameWhenSavingIsAvailable() {

        FakeSaveGame saveGame = new FakeSaveGame();

        GameMenu<String> menu = createMenu(
                "2\nMy Career\n1\n",
                saveGame
        );
        menu.show(true, "save data");

        assertEquals("My Career", saveGame.savedName);
        assertEquals("save data", saveGame.savedData);

        assertTrue(output.toString().contains("Game saved successfully."));
    }
    @Test
    void showLoadsExistingGame() {

        FakeSaveGame saveGame = new FakeSaveGame();

        saveGame.saveName = "My Career";
        saveGame.loadedData = "loaded data";

        GameMenu<String> menu = createMenu(
                "3\n1\n",
                saveGame
        );
        String result = menu.show(false, null);

        assertEquals("loaded data", result);

        assertTrue(output.toString().contains("Save loaded successfully."));
    }
    @Test
    void showDisplaysInvalidSaveSelection() {

        FakeSaveGame saveGame = new FakeSaveGame();

        saveGame.saveName = null;

        GameMenu<String> menu = createMenu(
                "3\n99\n1\n",
                saveGame
        );
        menu.show(false, null);

        assertTrue(output.toString().contains("Invalid save selection."));
    }
    @Test
    void showDeletesExistingGame() {

        FakeSaveGame saveGame = new FakeSaveGame();

        saveGame.saveName = "My Career";

        GameMenu<String> menu = createMenu(
                "4\n1\n1\n",
                saveGame
        );
        menu.show(false, null);

        assertTrue(saveGame.deleted);

        assertTrue(output.toString().contains("Save deleted successfully."));
    }
    @Test
    void showDisplaysSaveNotFoundWhenDeletingMissingGame() {

        FakeSaveGame saveGame = new FakeSaveGame();

        saveGame.saveName = null;

        GameMenu<String> menu = createMenu(
                "4\n1\n1\n",
                saveGame
        );
        menu.show(false, null);

        assertTrue(output.toString().contains("Save not found."));
    }
    @Test
    void showDisplaysHallOfFame() {

        HallOfFame hallOfFame = new HallOfFame();

        hallOfFame.saveResult(
                new GrandChampionPodium(
                        1,
                        "England",
                        185,
                        "France",
                        180,
                        "Brazil",
                        175
                )
        );
        GameMenu<String> menu = createMenu(
                "5\n1\n",
                new FakeSaveGame(),
                hallOfFame
        );
        menu.show(false, null);

        String result = output.toString();

        assertTrue(result.contains("HALL OF FAME"));
        assertTrue(result.contains("England"));
        assertTrue(result.contains("France"));
        assertTrue(result.contains("Brazil"));
    }
    private GameMenu<String> createMenu(
            String userInput,
            FakeSaveGame saveGame
    ) {
        return createMenu(
                userInput,
                saveGame,
                new HallOfFame()
        );
    }
    private GameMenu<String> createMenu(
            String userInput,
            FakeSaveGame saveGame,
            HallOfFame hallOfFame
    ) {
        System.setIn(
                new java.io.ByteArrayInputStream(
                        userInput.getBytes()
                )
        );

        return new GameMenu<>(
                new ConsolePrinter(),
                saveGame,
                hallOfFame,
                new Input()
        );
    }

    /**
     * Fake save implementation used to isolate GameMenu tests
     * from the real file system.
     */
    private static class FakeSaveGame extends SaveGame<String> {

        private String savedName;
        private String savedData;

        private String saveName;
        private String loadedData;

        private boolean deleted;

        FakeSaveGame() {
            super(SimulationType.LEAGUE_CAREER, String.class);
        }

        @Override
        public void save(String saveName, String saveData) {
            this.savedName = saveName;
            this.savedData = saveData;
        }
        @Override
        public File[] getSaveFiles() {
            return new File[]{
                    new File("My Career.json")
            };
        }
        @Override
        public String getSaveName(
                File[] saves,
                int selection
        ) {
            if (selection < 1 || selection > saves.length) {
                return null;
            }
            return saveName;
        }
        @Override
        public boolean saveExists(String saveName) {
            return saveName != null;
        }
        @Override
        public String load(String saveName) {
            return loadedData;
        }
        @Override
        public void deleteSave(String saveName) {
            deleted = true;
        }
    }
}