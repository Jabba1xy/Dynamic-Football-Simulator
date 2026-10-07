package service.engine;

import domain.model.Team;
import config.GameConstants;

import java.util.Random;

/**
 * Estimates match outcome probabilities using repeated simulations.
 *
 * <p>The predictor uses the Dynamic Uncertainty Model (DUM) match simulation
 * system. Team ratings influence the probability of scoring, with stronger
 * teams having a higher chance of successful scoring events. Multiple simulated
 * matches are played to estimate the likelihood of a home win, draw, or away win.</p>
 */
public class MatchPredictor {

    private final Team home;
    private final Team away;

    private final Random random;

    /**
     * Creates a predictor for two competing teams.
     *
     * <p>The random number generator is supplied externally so that prediction
     * outcomes can be controlled and reproduced during testing.</p>
     *
     * @param home the home team being predicted
     * @param away the away team being predicted
     * @param random random number generator used to determine prediction outcomes
     */
    public MatchPredictor(Team home, Team away, Random random) {
        this.home = home;
        this.away = away;
        this.random = random;
    }

    /**
     * Runs multiple simulated matches and estimates the resulting outcome probabilities.
     *
     * <p>Each simulation uses the same probability model as a normal match,
     * comparing random rolls against team ratings to determine scoring events.
     * The results of all simulations are converted into percentage probabilities.</p>
     *
     * @return an array containing:
     * <ul>
     *     <li>index 0 - home win percentage</li>
     *     <li>index 1 - draw percentage</li>
     *     <li>index 2 - away win percentage</li>
     * </ul>
     */
    public double[] predict() {

        int homeRating = home.getRating();
        int awayRating = away.getRating();

        int homeWins = 0;
        int awayWins = 0;
        int draws = 0;

        int max = Math.max(homeRating, awayRating) + GameConstants.RATING_VARIANCE_BUFFER;

        for (int a = 0; a < GameConstants.PREDICTION_SIMULATIONS; a++) {

            int homeGoals = 0;
            int awayGoals = 0;

            for (int x = 0; x < GameConstants.MATCH_LENGTH; x++) {

                int rollHome = random.nextInt(max); //
                int rollAway = random.nextInt(max);

                if (rollHome <= homeRating && rollAway > awayRating) {
                    homeGoals++;
                }
                else if (rollAway <= awayRating && rollHome > homeRating) {
                    awayGoals++;
                }
            }
            if (homeGoals > awayGoals) {
                homeWins++;
            } else if (awayGoals > homeGoals) {
                awayWins++;
            } else {
                draws++;
            }
        }
        return calculatePercentages(homeWins, draws, awayWins);
    }

    /**
     * Converts simulation results into outcome percentages.
     *
     * @param homeWins number of simulated home victories
     * @param draws number of simulated draws
     * @param awayWins number of simulated away victories
     * @return percentages representing home wins, draws, and away wins
     */
    private double[] calculatePercentages(int homeWins, int draws, int awayWins) {

        double homePct = homeWins / 10.0;
        double drawPct = draws / 10.0;
        double awayPct = awayWins / 10.0;

        return new double[] {homePct, drawPct, awayPct};
    }
}
