package domain.enums;

/**
 * Represents the different simulation modes and application states
 * supported by the football simulator.
 *
 * <p>The simulation type is used to determine which rules and rating
 * behaviour should be applied by the simulation and its supporting classes.</p>
 */
public enum SimulationType {
    LEAGUE_CAREER,
    WORLD_CUP,
    EXIT,
    INVALID
}