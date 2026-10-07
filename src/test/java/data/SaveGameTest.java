package data;

import domain.enums.SimulationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveGameTest {

    @TempDir
    Path tempDirectory;

    private String originalUserHome;
    private SaveGame<TestSaveData> saveGame;

    @BeforeEach
    void setUp() {

        originalUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDirectory.toString());

        saveGame = new SaveGame<>(
                SimulationType.LEAGUE_CAREER,
                TestSaveData.class
        );
    }

    @AfterEach
    void tearDown() {
        System.setProperty("user.home", originalUserHome);
    }

    @Test
    void saveCreatesSaveFile() {

        TestSaveData data = new TestSaveData("Test Career", 5);

        saveGame.save("test-save", data);

        assertTrue(saveGame.saveExists("test-save"));
    }
    @Test
    void loadRestoresSavedData() {

        TestSaveData data = new TestSaveData("Test Career", 5);

        saveGame.save("test-save", data);

        TestSaveData loaded = saveGame.load("test-save");

        assertNotNull(loaded);
        assertEquals("Test Career", loaded.name());
        assertEquals(5, loaded.season());
    }
    @Test
    void loadReturnsNullWhenSaveDoesNotExist() {

        assertNull(saveGame.load("missing-save"));
    }
    @Test
    void saveExistsReturnsFalseWhenSaveDoesNotExist() {

        assertFalse(saveGame.saveExists("missing-save"));
    }
    @Test
    void deleteSaveRemovesExistingSave() {

        TestSaveData data = new TestSaveData("Test Career", 5);

        saveGame.save("test-save", data);

        assertTrue(saveGame.saveExists("test-save"));

        saveGame.deleteSave("test-save");

        assertFalse(saveGame.saveExists("test-save"));
    }
    @Test
    void getSaveFilesReturnsAvailableSaves() {

        saveGame.save(
                "save-one",
                new TestSaveData("Career One", 1)
        );

        saveGame.save(
                "save-two",
                new TestSaveData("Career Two", 2)
        );

        File[] saves = saveGame.getSaveFiles();

        assertEquals(2, saves.length);
    }
    @Test
    void getSaveNameReturnsNameForValidSelection() {

        saveGame.save(
                "test-save",
                new TestSaveData("Test Career", 5)
        );

        File[] saves = saveGame.getSaveFiles();

        assertEquals("test-save", saveGame.getSaveName(saves, 1));
    }
    @Test
    void getSaveNameReturnsNullForInvalidSelection() {

        File[] saves = saveGame.getSaveFiles();

        assertNull(saveGame.getSaveName(saves, 0));
        assertNull(saveGame.getSaveName(saves, 1));
    }
    private record TestSaveData(String name, int season) {
    }
}