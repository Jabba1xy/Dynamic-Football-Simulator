package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.enums.SimulationType;

import java.io.File;
import java.io.IOException;

/**
 * Handles saving, loading, checking, and deleting simulation save data.
 *
 * <p>Save data is serialised to JSON using Jackson and stored in a
 * simulation-specific directory within the user's home directory.</p>
 *
 * <p>The generic type parameter determines the type of save data handled
 * by this instance.</p>
 *
 * @param <T> the type of save data managed by this class
 */
public class SaveGame<T> {

    private final String saveDirectory;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> saveDataType;

    /**
     * Creates a save manager for the specified simulation type.
     *
     * <p>A dedicated save directory is created for the simulation if it
     * does not already exist.</p>
     *
     * @param type the simulation type determining the save directory
     * @param saveDataType the class representing the save data type
     */
    public SaveGame(SimulationType type, Class<T> saveDataType) {

        this.saveDataType = saveDataType;

        saveDirectory =
                System.getProperty("user.home")
                + "/Dynamic Football Simulator/saves/"
                + type.name().toLowerCase().replace('_', ' ')
                + "/";

        File directory = new File(saveDirectory);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException(
                    "Unable to create save directory: " + saveDirectory
            );
        }
    }

    /**
     * Saves the supplied simulation data as a JSON file.
     *
     * @param saveName name of the save file without the {@code .json} extension
     * @param saveData data to be saved
     */
    public void save(String saveName, T saveData) {

        File file = new File(
                saveDirectory + saveName + ".json"
        );
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, saveData);

        } catch (IOException e) {
            throw new RuntimeException("Unable to save game.", e);
        }
    }

    /**
     * Loads a saved simulation from a JSON file.
     *
     * @param saveName name of the save file without the {@code .json} extension
     * @return the loaded save data, or {@code null} if the save does not exist
     */
    public T load(String saveName) {

        File file = new File(
                saveDirectory + saveName + ".json"
        );

        if (!file.exists()) {
            return null;
        }

        try {
            return mapper.readValue(file, saveDataType);

        } catch (IOException e) {
            throw new RuntimeException("Unable to load game.", e);
        }
    }

    /**
     * Checks whether a save with the specified name exists.
     *
     * @param saveName name of the save to check
     * @return {@code true} if the save exists, otherwise {@code false}
     */
    public boolean saveExists(String saveName) {

        File file = new File(
                saveDirectory + saveName + ".json"
        );
        return file.exists();
    }

    /**
     * Deletes a saved simulation.
     *
     * @param saveName name of the save to delete
     */
    public void deleteSave(String saveName) {

        File file = new File(
                saveDirectory + saveName + ".json"
        );
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Unable to delete save.");
        }
    }

    /**
     * Returns the save files currently stored in the simulation's 'save' directory.
     *
     * @return an array containing the available save files, or an empty array
     * if the save directory does not exist
     */
    public File[] getSaveFiles() {

        File directory = new File(saveDirectory);

        if (!directory.exists()) {
            return new File[0];
        }
        return directory.listFiles();
    }

    /**
     * Returns the save name corresponding to a user's selection.
     *
     * @param saves available save files
     * @param selection one-based index of the selected save
     * @return the selected save name without the {@code .json} extension,
     * or {@code null} if the selection is invalid
     */
    public String getSaveName(File[] saves, int selection) {

        if (selection < 1 || selection > saves.length) {
            return null;
        }
        return saves[selection - 1]
                .getName()
                .replace(".json", "");
    }
}