package ui;

/**
 * Stores user-configurable simulation settings.
 *
 * <p>Controls optional presentation features such as cinematic mode,
 * which enables delays and additional pacing during console output.</p>
 */
public class GameSettings {

    private boolean cinematicMode = true;

    /**
     * Checks whether cinematic mode is enabled.
     *
     * @return {@code true} if delays and enhanced presentation are enabled,
     * otherwise {@code false}
     */
    public boolean isCinematicMode() {
        return cinematicMode;
    }

    /**
     * Enables or disables cinematic mode.
     *
     * @param cinematicMode {@code true} to enable simulation delays,
     *                      {@code false} to run without delays
     */
    public void setCinematicMode(boolean cinematicMode) {
        this.cinematicMode = cinematicMode;
    }
}