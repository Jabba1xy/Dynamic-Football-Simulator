package domain.enums;

/**
 * Represents the actions available from the simulation menu.
 *
 * <p>The selected action determines whether the user continues the
 * simulation, saves or loads a career, manages saved data, views the
 * Hall of Fame, or exits the application.</p>
 */
public enum MenuAction {
    CONTINUE,
    SAVE,
    LOAD,
    DELETE,
    HALL_OF_FAME,
    EXIT,
    INVALID
}