package domain.enums;

/**
 * Represents the league tiers and tournament tables used by the simulation.
 *
 * <p>Standard league tiers apply different league buffs which influence team
 * rating calculations. World Cup tables are also represented here so that
 * tournament groups can use the same league structure while applying their
 * own simulation rules.</p>
 */
public enum LeagueTier {
    // League tables
    CHAMPION(1.2f),
    DIAMOND(1.1f),
    PLATINUM(1.0f),
    QUALIFIERS(1.0f),

    // World cup tables
    TABLE_A(1.0f),
    TABLE_B(1.0f),
    TABLE_C(1.0f),
    TABLE_D(1.0f),
    TABLE_E(1.0f),
    TABLE_F(1.0f),
    TABLE_G(1.0f),
    TABLE_H(1.0f),
    TABLE_I(1.0f),
    TABLE_J(1.0f),
    TABLE_K(1.0f),
    TABLE_L(1.0f);
    private final float leagueBuff;

    /**
     * Creates a league tier with its associated rating multiplier.
     *
     * @param leagueBuff multiplier applied during team rating calculations
     */
    LeagueTier(float leagueBuff) {
        this.leagueBuff = leagueBuff;
    }

    /**
     * Returns the rating multiplier applied by this league tier.
     *
     * @return the league rating multiplier
     */
    public float getLeagueBuff() {
        return leagueBuff;
    }
}
