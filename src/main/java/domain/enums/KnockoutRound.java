package domain.enums;

/**
 * Represents the different rounds within a knockout tournament.
 *
 * <p>Each round is identified by the number of teams remaining in the
 * competition. This allows the knockout system to automatically determine
 * the current round name as teams are eliminated.</p>
 */
public enum KnockoutRound {

    FINAL(2, "FINAL"),
    SEMI_FINALS(4, "SEMI FINALS"),
    QUARTER_FINALS(8, "QUARTER FINALS"),
    ROUND_OF_16(16, "ROUND OF 16"),
    ROUND_OF_32(32, "ROUND OF 32");

    private final int teamCount;
    private final String displayName;

    KnockoutRound(int teamCount, String displayName) {
        this.teamCount = teamCount;
        this.displayName = displayName;
    }

    /**
     * Returns the display name for the knockout round matching the number
     * of teams remaining.
     *
     * <p>If the team count does not correspond to a defined knockout round,
     * a generic knockout round name is returned.</p>
     *
     * @param teamCount number of teams remaining in the tournament
     * @return the display name of the corresponding round
     */
    public static String getRoundName(int teamCount) {

        for (KnockoutRound round : values()) {
            if (round.teamCount == teamCount) {
                return round.displayName;
            }
        }
        return "KNOCKOUT ROUND";
    }
}