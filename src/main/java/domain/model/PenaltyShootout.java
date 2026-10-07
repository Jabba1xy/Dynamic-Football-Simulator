package domain.model;

import config.GameConstants;
import ui.ConsolePrinter;
import ui.GameSettings;

import java.util.Random;

/**
 * Simulates a penalty shootout when a knockout match remains level.
 *
 * <p>Penalties use the Dynamic Uncertainty Model (DUM) to determine whether
 * each attempt is scored. Team ratings influence the probability of scoring,
 * while an underdog adjustment reduces large rating differences to make
 * penalty shootouts less predictable.</p>
 */
public class PenaltyShootout {

    private final Team home;
    private final Team away;

    private final Random random;
    private final ConsolePrinter printer;

    /**
     * Creates a penalty shootout between two teams using the supplied
     * random number generator and console presentation component.
     *
     * <p>Dependencies are supplied externally to keep the penalty shootout
     * independent from random-number generation and console output, making
     * the class easier to test.</p>
     *
     * @param home the home team
     * @param away the away team
     * @param random random number generator used to determine penalty outcomes
     * @param printer handles console output and penalty shootout presentation
     */
    public PenaltyShootout(Team home, Team away, Random random, ConsolePrinter printer) {
        this.home = home;
        this.away = away;
        this.random = random;
        this.printer = printer;
    }

    /**
     * Plays a complete penalty shootout until a winner is determined.
     *
     * <p>Each team initially takes up to five penalties. The shootout can end
     * early if one team has an insurmountable lead. If the scores remain level
     * after the initial penalties, the shootout continues using sudden death
     * until a winner is determined.</p>
     *
     * @param settings controls console output and simulation delays
     * @return the winning team
     */
    public Team play(GameSettings settings) {

        printer.printMessage("\n!!! PENALTY SHOOTOUT !!!\n");
        printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

        int homePens = 0;
        int awayPens = 0;

        int homeRemaining = 5;
        int awayRemaining = 5;

        double homeRating = home.getRating();
        double awayRating = away.getRating();


        // 5 penalties each
        for (int i = 0; i < 5; i++) {

            if (takePenalty(homeRating, awayRating)) {
                homePens++;
                printer.printPenaltyShotResult(home, true);
                printer.pause(settings, GameConstants.THREE_SECOND_DELAY);

            } else {
                printer.printPenaltyShotResult(home, false);
                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
            }

            // Home has taken a penalty
            homeRemaining--;

            // Stop if impossible for away to catch up
            if (homePens > awayPens + awayRemaining) {
                break;
            }

            if (takePenalty(awayRating, homeRating)) {
                awayPens++;
                printer.printPenaltyShotResult(away, true);
                printer.pause(settings, GameConstants.THREE_SECOND_DELAY);

            } else {
                printer.printPenaltyShotResult(away, false);
                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
            }

            // Away has taken a penalty
            awayRemaining--;

            // Stop if impossible for home to catch up
            if (awayPens > homePens + homeRemaining) {
                break;
            }

            printer.printPenaltyScore(home, away, homePens, awayPens);
            printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
        }

        // Sudden death
        while (homePens == awayPens) {

            printer.printMessage("\nSUDDEN DEATH...");
            printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

            boolean homeScore = takePenalty(homeRating, awayRating);
            boolean awayScore = takePenalty(awayRating, homeRating);

            printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

            if (homeScore) {
                homePens++;
                printer.printPenaltyShotResult(home, true);
                printer.pause(settings, GameConstants.THREE_SECOND_DELAY);

            } else {
                printer.printPenaltyShotResult(home, false);
                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
            }

            if (awayScore) {
                awayPens++;
                printer.printPenaltyShotResult(away, true);
                printer.pause(settings, GameConstants.THREE_SECOND_DELAY);

            } else {
                printer.printPenaltyShotResult(away, false);
                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
            }

            printer.printPenaltyScore(home, away, homePens, awayPens);
            printer.newLine();
            printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
        }

        printer.printPenaltyFinalScore(home, away, homePens, awayPens);
        printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

        if (homePens > awayPens) {

            printer.penaltyWinner(home);
            printer.newLine();
            printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

            return home;

        } else {

            printer.penaltyWinner(away);
            printer.newLine();
            printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

            return away;
        }
    }

    /**
     * Simulates a single penalty attempt.
     *
     * <p>The Dynamic Uncertainty Model (DUM) compares a random shooter roll
     * against a random goalkeeper roll. Team ratings influence both rolls.
     * To reduce the impact of large rating differences, the lower-rated side
     * receives half of the rating gap as an effective rating adjustment.
     * The shooter also receives a small scoring advantage to reflect the
     * natural advantage of taking the penalty.</p>
     *
     * @param shooterRating rating of the team taking the penalty
     * @param keeperRating rating of the opposing team
     * @return {@code true} if the penalty is scored; otherwise {@code false}
     */
    private boolean takePenalty(double shooterRating, double keeperRating) {

        int difference  = (int) Math.abs(shooterRating - keeperRating);

        if (shooterRating < keeperRating) {
            shooterRating += difference  / 2.0;
        }

        if (keeperRating < shooterRating) {
            keeperRating += difference  / 2.0;
        }

        int shooterRoll = random.nextInt(GameConstants.PENALTY_VARIANCE_BUFFER)
                + (int) shooterRating;

        int keeperRoll = random.nextInt(GameConstants.PENALTY_VARIANCE_BUFFER)
                + (int) keeperRating;

        return shooterRoll + GameConstants.SHOOTER_BONUS >= keeperRoll;
    }
}