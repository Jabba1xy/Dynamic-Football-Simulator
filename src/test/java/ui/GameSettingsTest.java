package ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSettingsTest {

    @Test
    void cinematicModeIsEnabledByDefault() {

        GameSettings settings = new GameSettings();

        assertTrue(settings.isCinematicMode());
    }
    @Test
    void setCinematicModeDisablesCinematicMode() {

        GameSettings settings = new GameSettings();

        settings.setCinematicMode(false);

        assertFalse(settings.isCinematicMode());
    }
    @Test
    void setCinematicModeEnablesCinematicMode() {

        GameSettings settings = new GameSettings();

        settings.setCinematicMode(false);
        settings.setCinematicMode(true);

        assertTrue(settings.isCinematicMode());
    }
}