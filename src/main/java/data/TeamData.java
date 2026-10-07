package data;

/**
 * Stores the base data used to create a team.
 *
 * <p>The strength multiplier allows individual teams to have a predefined
 * strength adjustment when they are created by {@code TeamFactory}.</p>
 *
 * @param name the team's name
 * @param strengthMultiplier the team's initial strength multiplier
 */
public record TeamData(String name, float strengthMultiplier) {
}