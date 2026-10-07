package config;

/**
 * Centralised configuration values used throughout the simulation.
 *
 * <p>This class stores shared constants for match simulation, rating calculations,
 * league progression, console timing, and tournament rules. Keeping these values
 * in one place prevents magic numbers from being scattered throughout the codebase
 * and makes future balancing changes easier.</p>
 *
 * <p>This class cannot be instantiated.</p>
 */
public final class GameConstants {

    private GameConstants() {}

    public static final int THREE_SECOND_DELAY = 3000;
    public static final int TWO_SECOND_DELAY = 2000;
    public static final int ONE_SECOND_DELAY = 1000;
    public static final int PREDICTION_SIMULATIONS = 1000;
    public static final int MAX_HISTORY = 100;
    public static final int MINIMUM_RATING = 100;
    public static final int PENALTY_VARIANCE_BUFFER = 60; // Reduces rating advantage in shootouts
    public static final int RATING_PROTECTION_THRESHOLD = 30; // Rating gap considered significant
    public static final int RATING_VARIANCE_BUFFER = 30; // Allows lower-rated teams an opportunity to win
    public static final int WORLD_CUP_QUALIFIER_SIZE = 25;
    public static final int GRAND_CHAMP_BUFF = 10;
    public static final int DEFAULT_LEAGUE_SIZE = 10;
    public static final int SHOOTER_BONUS = 10;
    public static final int MATCH_LENGTH = 8;
    public static final int GROUP_STAGE_SIZE = 4;
    public static final int LEAGUE_GRAND_CHAMP_SPOTS = 4;
    public static final int LEAGUE_PROMOTION_SPOTS = 3;
    public static final int EXTRA_TIME_LENGTH = 3;
    public static final int GROUP_STAGE_TABLE_SIZE = 3;
}