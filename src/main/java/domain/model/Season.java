package domain.model;

import config.GameConstants;
import domain.enums.SimulationType;
import ui.ConsolePrinter;
import ui.GameSettings;
import ui.Input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Controls the progression of football league seasons and tournament stages.
 *
 * <p>The Season class is responsible for managing match weeks, running
 * fixtures, updating league standings, and triggering team rating
 * recalculations after each round.</p>
 *
 * <p>A season can contain multiple leagues. Each league is simulated
 * independently while sharing the same scheduling and progression rules.
 * Individual match simulation is handled by {@link Match}.</p>
 *
 * <p>After each match week, teams are ranked using points, goal difference,
 * goals scored, and rating. Their position is then used to calculate a
 * position bonus before their rating is recalculated through the Dynamic
 * Rating Equation.</p>
 *
 * <p>The supplied {@link SimulationType} determines which position-bonus
 * and rating rules are applied during the simulation.</p>
 */
public class Season {

    private final ConsolePrinter printer;
    private final Input input;

    /**
     * Creates a season controller using the supplied console presentation
     * and input components.
     *
     * <p>The dependencies are supplied externally rather than created internally,
     * keeping the season controller loosely coupled to console I/O and making
     * it easier to test.</p>
     *
     * @param printer handles console output and presentation
     * @param input handles console user input during the season
     */
    public Season(ConsolePrinter printer, Input input) {
        this.printer = printer;
        this.input = input;
    }

    /**
     * Simulates a season across the supplied leagues.
     *
     * <p>Each league is processed independently. Match results can optionally
     * contribute to each team's long-term statistics and match history.</p>
     *
     * @param leagues leagues to simulate
     * @param seasonSize number of match weeks to play
     * @param settings controls console output and simulation delays
     * @param type determines the simulation-specific rating behaviour
     * @param calculateTotal determines whether match results contribute to
     * long-term team statistics
     */
    public void playSeason(List<League> leagues, int seasonSize, GameSettings settings, SimulationType type, boolean calculateTotal) {
        for (League league : leagues) {

            printer.askedToPressEnter();
            input.pressEnter();

            playLeague(league, seasonSize, settings, type, calculateTotal);
        }
    }

    /**
     * Simulates the scheduled match weeks for a single league.
     *
     * <p>Each round pairs teams using a rotating fixture schedule. Matches are
     * simulated, league standings are updated, and team ratings are recalculated
     * after every match week.</p>
     *
     * @param league league being simulated
     * @param seasonSize number of match weeks to play
     * @param settings controls console output and simulation delays
     * @param type determines the simulation-specific position-bonus and rating behaviour
     * @param calculateTotal determines whether match results contribute to
     * long-term team statistics
     */
    private void playLeague(League league, int seasonSize, GameSettings settings, SimulationType type, boolean calculateTotal) {

        List<Team> schedule = new ArrayList<>(league.getTeams());

        printer.printLeagueHeader(league);

        // LEAGUE SEASON
        for (int round = 0; round < seasonSize; round++) {

            printer.askedToPressEnter();
            input.pressEnter();

            printer.printMatchWeek(round + 1);
            printer.pause(settings, 1000);

            // MATCH WEEK
            for (int i = 0; i < (league.size() / 2); i++) {

                Team home = schedule.get(i);
                Team away = schedule.get(schedule.size() - 1 - i);

                Match match = new Match(home, away, new Random(), printer);
                match.play(settings, calculateTotal);
            }
            updateLeagueStandings(league.getTeams(), type);
            printer.printLeagueTable(league.getTeams(), league.getTier());
            schedule = rotate(schedule);
        }
    }

    /**
     * Updates league rankings and recalculates team ratings.
     *
     * <p>Teams are ranked by points, goal difference, goals scored, and
     * current rating. A position bonus is then assigned according to the
     * simulation type before each team's rating is recalculated.</p>
     *
     * <p>The simulation type determines how a team's position contributes
     * to its rating calculation.</p>
     *
     * @param teams teams whose standings and ratings are being updated
     * @param type determines the position-bonus and rating behaviour
     */
    private static void updateLeagueStandings(List<Team> teams, SimulationType type) {
        teams.sort(
                Comparator.comparingInt(Team::getPoints)
                        .thenComparingInt(Team::getGoalDifference)
                        .thenComparingInt(Team::getGoalsFor)
                        .thenComparingInt(Team::getRating)
                        .reversed()
        );
        int size = teams.size();

        for (int i = 0; i < size; i++) {

            int bonus;
            Team team = teams.get(i);

            if (type != SimulationType.WORLD_CUP) {
                bonus = (size - 1 - i);
                team.setPositionBuff(bonus);
            } else {
                int maxTier = (size / 5) - 1;

                bonus = maxTier - (i / 5);
                team.setPositionBuff(bonus);
            }

            team.calculateRating(
                    type, size <= GameConstants.GROUP_STAGE_SIZE);
        }
    }

    /**
     * Rotates the fixture list to generate the next round of pairings.
     */
    private static List<Team> rotate(List<Team> list) {

        List<Team> rotated = new ArrayList<>(list);

        Team last = rotated.remove(rotated.size() - 1);
        rotated.add(1, last);

        return rotated;
    }
}