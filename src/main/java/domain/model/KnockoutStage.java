package domain.model;

import domain.dto.KnockoutResult;
import domain.enums.KnockoutRound;
import config.GameConstants;
import domain.enums.SimulationType;
import ui.ConsolePrinter;
import ui.GameSettings;
import ui.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages knockout-stage matches and determines the final tournament standings.
 *
 * <p>The knockout stage progresses through successive rounds until a winner
 * and runner-up are determined. Drawn matches are resolved through extra time
 * and, when necessary, penalty shootouts. In league career mode, teams are
 * shuffled before the tournament begins.</p>
 *
 * <p>The two teams eliminated in the semi-finals compete in a third-place
 * playoff.</p>
 *
 * <p>User input is supplied through an {@link Input} dependency, allowing
 * console interaction to be controlled and replaced during testing.</p>
 */
public class KnockoutStage {

    private final ConsolePrinter printer;
    private final Input input;

    /**
     * Creates a knockout-stage manager using the supplied console presentation
     * and input components.
     *
     * <p>The dependencies are provided by the caller rather than being created
     * internally, keeping the class loosely coupled to console I/O and making
     * the knockout stage easier to test.</p>
     *
     * @param printer handles console output and presentation
     * @param input handles user input during knockout stages
     */
    public KnockoutStage(ConsolePrinter printer, Input input) {
        this.printer = printer;
        this.input = input;
    }

    /**
     * Runs a knockout tournament using the supplied teams.
     *
     * <p>Teams are paired into fixtures and progress through successive rounds
     * until a champion and runner-up are determined. Drawn matches are resolved
     * through extra time and, if still level, a penalty shootout.</p>
     *
     * <p>The losing teams from the semi-finals compete in a third-place playoff.
     * Team statistics are reset between knockout rounds because knockout matches
     * use their own results rather than league standings.</p>
     *
     * <p>Teams are shuffled before the tournament when running the league career
     * simulation. World Cup knockout teams retain their existing order.</p>
     *
     * @param teams teams participating in the knockout tournament
     * @param settings controls console output and simulation delays
     * @param type simulation type determining tournament behaviour
     * @return the champion, runner-up, and third-place team
     * @throws IllegalArgumentException if the number of teams is not a power of two
     */
    public KnockoutResult runKnockout(List<Team> teams, GameSettings settings, SimulationType type) {

        List<Team> semiFinalLosers = new ArrayList<>();

        Team champion = null;
        Team runnerUp = null;
        Team thirdPlace = null;

        validateKnockoutSize(teams);

        if (type == SimulationType.LEAGUE_CAREER) {
            Collections.shuffle(teams);
        }

        resetTeamStats(teams);

        printer.printGrandChampTitle();

        while (teams.size() > 1) {

            List<Team> nextRound = new ArrayList<>();

            if (teams.size() == 2) {
                resetTeamStats(semiFinalLosers);
                thirdPlace = playThirdPlaceMatch(semiFinalLosers, settings);
            }

            printer.printMessage("\n======================");
            printer.printMessage(
                    KnockoutRound.getRoundName(teams.size())
            );
            printer.printMessage("======================\n");

            printer.printFixtures(teams);
            printer.askedToPressEnter();
            input.pressEnter();

            for (int i = 0; i < teams.size(); i += 2) {

                Team a = teams.get(i);

                Team b = teams.get(i + 1);

                Team winner;
                Team loser;

                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

                Match match = new Match(a, b, new Random(), printer);
                match.play(settings, false);

                if (a.getGoalsFor() > b.getGoalsFor()) {
                    winner = a;
                    loser = b;
                }
                else if (a.getGoalsFor() < b.getGoalsFor()) {
                    winner = b;
                    loser = a;
                }
                else {
                    winner = match.extraTime(settings);
                    loser = winner == a ? b : a;
                }
                if (teams.size() == 4) {
                    semiFinalLosers.add(loser);
                }
                else if (teams.size() == 2) {
                    champion = winner;
                    runnerUp = loser;
                }
                nextRound.add(winner);
                printer.pause(settings, GameConstants.TWO_SECOND_DELAY);
            }
            teams = nextRound;
            resetTeamStats(teams);
        }
        printer.pause(settings, GameConstants.ONE_SECOND_DELAY);

        return new KnockoutResult(champion, runnerUp, thirdPlace);
    }

    /**
     * Validates that the number of knockout participants can form a complete
     * elimination bracket.
     *
     * <p>A valid tournament must contain at least four teams and use a
     * power-of-two number of teams, such as 4, 8, 16, or 32.</p>
     *
     * @param teams teams participating in the knockout tournament
     * @throws IllegalArgumentException if the number of teams is not a power of two
     */
    private static void validateKnockoutSize(List<Team> teams) {

        int size = teams.size();

        if (size < 4 || (size & (size - 1)) != 0) {
            throw new IllegalArgumentException(
                    "Knockout size must have at least 4 teams be a power of 2 (4, 8, 16...)"
            );
        }
    }

    /**
     * Plays the third-place playoff between the two semi-final losers.
     *
     * <p>If the match is level after normal time, extra time and penalties
     * are used to determine the winner.</p>
     *
     * @param teams the two teams competing for third place
     * @param settings controls console output and simulation delays
     * @return the team that finishes in third place
     */
    private Team playThirdPlaceMatch(List<Team> teams, GameSettings settings) {
        Team home = teams.get(0);
        Team away = teams.get(1);

        printer.printMessage("\n======================");
        printer.printMessage("THIRD PLACE PLAYOFF");
        printer.printMessage("======================\n");

        printer.askedToPressEnter();
        input.pressEnter();

        printer.pause(settings, GameConstants.TWO_SECOND_DELAY);

        Match match = new Match(home, away, new Random(), printer);
        match.play(settings, false);

        if (home.getGoalsFor() > away.getGoalsFor()) {
            return home;
        }
        if (away.getGoalsFor() > home.getGoalsFor()) {
            return away;
        }
        return match.extraTime(settings);
    }
    private static void resetTeamStats(List<Team> list) {
        for (Team team : list) {
            team.resetStats();
        }
    }
}
