package domain.enums;

/**
 * Represents the possible outcomes of a football match and their associated
 * league points.
 *
 * <p>A win awards three points, a draw awards one point, and a loss awards
 * no points.</p>
 */
public enum MatchOutcome {
    WIN(3),
    DRAW(1),
    LOSS(0);

    private final int points;

    /**
     * Creates a match outcome with its associated league points.
     *
     * @param points the number of points awarded for this outcome
     */
    MatchOutcome(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}