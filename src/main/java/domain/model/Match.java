package domain.model;

import config.GameConstants;
import service.management.MatchResult;
import ui.ConsolePrinter;
import ui.GameSettings;

import java.util.Random;

/**
 * Represents a football match between two teams.
 *
 * <p>A match simulates goals using the teams' current ratings, applies
 * the resulting outcome to both teams, and can resolve drawn knockout
 * matches through extra time and penalty shootouts.</p>
 */
public class Match {

    private final Team home;
    private final Team away;

    private int homeGoals = 0;
    private int awayGoals = 0;

    private final Random random;
    private final ConsolePrinter printer;

    /**
     * Creates a match between two teams using the supplied random number
     * generator and console presentation component.
     *
     * <p>Dependencies are supplied externally to keep match simulation
     * independent from console output and random-number generation, making
     * the class easier to test.</p>
     *
     * @param home the home team
     * @param away the away team
     * @param random random number generator used to determine match events
     * @param printer handles console output and match presentation
     */
    public Match(Team home, Team away, Random random, ConsolePrinter printer) {
        this.home = home;
        this.away = away;
        this.random = random;
        this.printer = printer;
    }

    /**
     * Simulates a match between two teams and applies the resulting statistics.
     *
     * <p>The match outcome is determined using the Dynamic Uncertainty Match Model (DUMM),
     * which uses each team's current rating to calculate scoring probabilities.
     * Higher-rated teams have a greater probability of scoring, while the variance
     * buffer allows lower-rated teams an opportunity to win.</p>
     *
     * <p>The team's rating is calculated by the Dynamic Rating Equation (DRE).
     * The DRE therefore directly influences the probabilities used by the DUMM.
     * Match results then update the team's performance statistics, which are used
     * by future DRE calculations to dynamically adjust the team's rating.</p>
     *
     * <p>After the simulation completes, the resulting {@link MatchResult}
     * applies the outcome to both teams.</p>
     *
     * @param settings controls console output and simulation delays
     * @param calculateTotal determines whether the result contributes to the team's
     * long-term statistics and match history
     */
    public void play(GameSettings settings, boolean calculateTotal) {

        homeGoals = 0;
        awayGoals = 0;

        int homeRating = home.getRating();
        int awayRating = away.getRating();

        int max = Math.max(homeRating, awayRating) + GameConstants.RATING_VARIANCE_BUFFER;

        printer.printMatchStart(home, away, settings, random);
        printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

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
        printer.printFinalMatchResult(home, away, homeGoals, awayGoals);
        printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

        MatchResult result = new MatchResult(home, away, homeGoals, awayGoals);
        result.apply(calculateTotal);
    }

    /**
     * Resolves a drawn knockout match after normal time.
     *
     * <p>If the match remains level after extra time, a penalty shootout is
     * used to determine the winner.</p>
     *
     * @param settings controls simulation output and delays
     * @return the team that wins after extra time or penalties
     */
    public Team extraTime(GameSettings settings) {

        if (homeGoals == awayGoals) {
            double homeRating = home.getRating();
            double awayRating = away.getRating();

            int max = (int) Math.max(homeRating, awayRating)
                    + GameConstants.RATING_VARIANCE_BUFFER;

            printer.pause(settings, GameConstants.ONE_SECOND_DELAY);
            printer.printMessage("!!!EXTRA TIME!!!");
            printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

            for (int x = 0; x < GameConstants.EXTRA_TIME_LENGTH; x++) {

                int rollHome = random.nextInt(max); //
                int rollAway = random.nextInt(max);

                if (rollHome <= homeRating && rollAway > awayRating) {
                    homeGoals++;
                }
                else if (rollAway <= awayRating && rollHome > homeRating) {
                    awayGoals++;
                }
            }
        }
        printer.printFinalMatchResult(home, away, homeGoals, awayGoals);

        if (homeGoals > awayGoals) {
            return home;
        }
        if (awayGoals > homeGoals) {
            return away;
        }
        else {
            PenaltyShootout penalties = new PenaltyShootout(home, away, random, printer);
            return penalties.play(settings);
        }
    }
}
