package service.management;

import domain.model.Team;
import config.GameConstants;

public class MatchResult {

    private final Team home;
    private final Team away;

    private final int homeGoals;
    private final int awayGoals;

    /**
     * Processes the outcome of a completed football match.
     *
     * <p>This class separates match simulation from team progression by
     * handling the effects of a result after the Dynamic Uncertainty Model
     * (DUM) has simulated the scoreline.</p>
     *
     * <p>The result updates team statistics, recent performance history,
     * and rating buffs based on the outcome and rating disparity between
     * the two teams.</p>
     */
    public MatchResult(Team home, Team away, int homeGoals, int awayGoals) {
        this.home = home;
        this.away = away;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    /**
     * Applies the match outcome to both teams.
     *
     * <p>The winning, drawing, or losing team's statistics are updated,
     * including points, wins, losses, goals, and rolling performance history.</p>
     *
     * <p>When enabled, rating buffs are calculated using the rating disparity
     * between the teams. Underdog victories provide larger rating adjustments,
     * while draws against significantly stronger teams can also reward the
     * lower-rated team.</p>
     *
     * @param calculateTotal whether the result should contribute to long-term
     * team statistics and rating adjustments
     */
    public void apply(boolean calculateTotal) {

        int ratingDiffAbs = Math.abs(home.getRating() - away.getRating());

        // ADDING TEAM STATS
        if (homeGoals > awayGoals) {
            home.recordWin(homeGoals, awayGoals, calculateTotal);
            away.recordLoss(awayGoals, homeGoals, calculateTotal);
        } else if (awayGoals > homeGoals) {
            away.recordWin(awayGoals, homeGoals, calculateTotal);
            home.recordLoss(homeGoals, awayGoals, calculateTotal);
        } else {
            home.recordDraw(homeGoals, awayGoals, calculateTotal);
            away.recordDraw(awayGoals, homeGoals, calculateTotal);
        }
        // DECIDING THE RATING BUFF
        if (calculateTotal) {
            if (homeGoals > awayGoals && home.getRating() < away.getRating()) {
                home.addRatingBuff(division(ratingDiffAbs, 2));
                away.addRatingBuff(-division(ratingDiffAbs, 2));

            } else if (awayGoals > homeGoals && away.getRating() < home.getRating()) {
                away.addRatingBuff(division(ratingDiffAbs, 2));
                home.addRatingBuff(-division(ratingDiffAbs, 2));

            } else if (homeGoals == awayGoals && ratingDiffAbs >= GameConstants.RATING_PROTECTION_THRESHOLD / 2 && ratingDiffAbs <= GameConstants.RATING_PROTECTION_THRESHOLD) {

                if (home.getRating() < away.getRating()) {
                    home.addRatingBuff(division(ratingDiffAbs, 4));
                    away.addRatingBuff(-division(ratingDiffAbs, 4));
                }
                if (home.getRating() > away.getRating()) {
                    away.addRatingBuff(division(ratingDiffAbs, 4));
                    home.addRatingBuff(-division(ratingDiffAbs, 4));
                }

            } else if (homeGoals == awayGoals && ratingDiffAbs > GameConstants.RATING_PROTECTION_THRESHOLD) {

                if (home.getRating() < away.getRating()) {
                    home.addRatingBuff(division(ratingDiffAbs, 2));
                    away.addRatingBuff(-division(ratingDiffAbs, 2));
                } else {
                    away.addRatingBuff(division(ratingDiffAbs, 2));
                    home.addRatingBuff(-division(ratingDiffAbs, 2));
                }
            }
        }
    }
    private static int division(int dividing, int dividingBy) {
        return dividing / dividingBy;
    }
}