package ui;

import domain.dto.GrandChampionPodium;
import domain.enums.LeagueTier;
import domain.model.League;
import domain.model.Team;
import config.GameConstants;
import service.engine.MatchPredictor;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Handles all console-based output for the football simulation.
 *
 * <p>This class separates presentation logic from the simulation engine,
 * allowing domain classes such as {@link Team} and match systems to focus
 * on simulation and calculations rather than console formatting.</p>
 *
 * <p>Responsible for displaying menus, fixtures, match results, league
 * tables, penalty shootouts, championship messages, save information,
 * Hall of Fame results, and cinematic delays.</p>
 */
public class ConsolePrinter {

    /**
     * Displays the main simulator mode selection menu.
     */
    public void printSimulatorMenu() {

        printMessage("""
            
            FOOTBALL SIMULATOR
            
            1. League Career
            2. World Cup
            3. Exit
            
            """);
    }
    /**
     * Displays the current round of knockout fixtures.
     *
     * @param teams teams participating in the current knockout round
     */
    public void printFixtures(List<Team> teams) {

        printMessage("FIXTURES:");

        for (int i = 0; i < teams.size(); i += 2) {
            printMessage(
                    teams.get(i).getRating() + " " + teams.get(i).getName() +
                            " vs " +
                            teams.get(i + 1).getName() + " " + teams.get(i + 1).getRating()
            );
        }
        newLine();
    }

    /**
     * Displays the starting information for a match, including team ratings
     * and predicted outcome probabilities.
     *
     * <p>The prediction is generated using {@link MatchPredictor}, which uses
     * the Dynamic Uncertainty Model (DUM) to estimate possible match outcomes
     * based on team ratings.</p>
     *
     * @param home the home team
     * @param away the away team
     * @param settings controls cinematic output delays
     */
    public void printMatchStart(Team home, Team away, GameSettings settings, Random random) {

        printMessage(home.getRating() + " " + home.getName()
                + " vs "
                + away.getName() + " " + away.getRating());

        pause(settings, GameConstants.ONE_SECOND_DELAY);

        MatchPredictor probability = new MatchPredictor(home, away, random);
        double[] percentages = probability.predict();

        double homeChance = percentages[0];
        double drawChance = percentages[1];
        double awayChance = percentages[2];

        String outputMessage = String.format("%s %.1f%% -- %.1f%% -- %.1f%% %s",
                home.getName(),
                homeChance,
                drawChance,
                awayChance,
                away.getName()
        );
        printMessage(outputMessage);
    }

    /**
     * Displays the completed scoreline and match winner.
     *
     * @param home the home team
     * @param away the away team
     * @param homeGoals goals scored by the home team
     * @param awayGoals goals scored by the away team
     */
    public void printFinalMatchResult(Team home, Team away, int homeGoals, int awayGoals) {

        printMessage("\nFINAL SCORE:");
        printMessage(home.getRating() + " " + home.getName() + " " + homeGoals +
                " - " + awayGoals + " " + away.getName() + " " + away.getRating());

        if (homeGoals > awayGoals) {
            printMessage(home.getName() + " WIN");
        } else if (awayGoals > homeGoals) {
            printMessage(away.getName() + " WIN");
        } else {
            printMessage("DRAW");
        }
        newLine();
    }

    /**
     * Displays the current league standings.
     *
     * <p>The table includes team performance statistics and current
     * ratings.</p>
     *
     * @param teams teams currently in the league table
     * @param tier league tier being displayed
     */
    public void printLeagueTable(List<Team> teams, LeagueTier tier) {

        printMessage(tier.name().replace('_', ' ') + " LEAGUE!");
        printMessage("-------------------------------------------------------------");
        System.out.printf("%-4s %-15s %3s %3s %3s %3s %3s %3s %3s %4s %3s %3s%n",
                "Pos", "Team", "PT", "W", "D", "L", "GF", "GA", "GD", "GPG", "P", "R");
        printMessage("-------------------------------------------------------------");

        int pos = 1;

        for (Team t : teams) {

            String gpg = centre(formatDecimal(t.getGoalsPerGame()));

            System.out.printf("%-4d %-15s %3d %3d %3d %3d %3d %3d %3d %4s %2d %4d%n",
                    pos++,
                    t.getName(),
                    t.getPoints(),
                    t.getWins(),
                    t.getDraws(),
                    t.getLosses(),
                    t.getGoalsFor(),
                    t.getGoalsAgainst(),
                    t.getGoalDifference(),
                    gpg,
                    t.getPlayed(),
                    t.getRating());
        }
        newLine();
    }

    /**
     * Displays the standings for each league in the league system.
     *
     * @param leagues leagues to display
     */
    public void printLeagueSystem(List<League> leagues) {

        for (League league : leagues) {

            printLeagueTable(
                    league.getTeams(),
                    league.getTier()
            );
            newLine();
        }
    }

    /**
     * Displays a heading for the specified league.
     *
     * @param league league whose tier is displayed
     */
    public void printLeagueHeader(League league) {
        printMessage("\n========== " + league.getTier() + " LEAGUE! ==========\n");
    }

    /**
     * Displays the current match week.
     *
     * @param week match week number
     */
    public void printMatchWeek(int week) {
        printMessage("\n========== MATCH WEEK " + week + " ==========\n");
    }

    /**
     * Displays promoted and relegated teams after league completion.
     *
     * @param winners teams moving to the higher league tier
     * @param losers teams moving to the lower league tier
     * @param tier league tier that has completed its season
     * @param settings controls cinematic output delays
     */
    public void printLeagueWinnersAndLosers(List<Team> winners, List<Team> losers, LeagueTier tier, GameSettings settings) {

        pause(settings, GameConstants.ONE_SECOND_DELAY);
        printMessage("WINNERS OF " + tier + ":");
        for (Team team : winners) {
            printMessage(team.getRating() + " " + team.getName());
        }

        pause(settings, GameConstants.ONE_SECOND_DELAY);
        printMessage("LOSERS OF " + tier + ":");
        for (Team team : losers) {
            printMessage(team.getRating() + " " + team.getName());
        }
        pause(settings, GameConstants.TWO_SECOND_DELAY);
    }

    /**
     * Displays the winner of the Grand Championship.
     *
     * @param winner team that won the knockout championship
     */
    public void printGrandChampion(Team winner) {

        printMessage(
                "THE GRAND CHAMPION WINNERS ARE "
                        + winner.getRating()
                        + " "
                        + winner.getName().toUpperCase()
                        + "!");
    }

    /**
     * Prompts the user to press ENTER to continue.
     */
    public void askedToPressEnter() {
        printMessage("Press ENTER to continue...");
    }

    /**
     * Prints a message to the console.
     *
     * @param message message to display
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the outcome of an individual penalty attempt.
     *
     * @param team team taking the penalty
     * @param scored whether the penalty was successful
     */
    public void printPenaltyShotResult(Team team, boolean scored) {
        if (scored) {
            printMessage(team.getName().toUpperCase() + " GOAL!");
        }
        else {
            printMessage(team.getName().toUpperCase() + " MISSES!");
        }
    }

    /**
     * Displays the current penalty shootout score.
     *
     * @param home home team
     * @param away away team
     * @param homeGoals home penalty goals scored
     * @param awayGoals away penalty goals scored
     */
    public void printPenaltyScore(Team home, Team away, int homeGoals, int awayGoals) {
        printMessage(home.getName() + " " + homeGoals + " - "
                + awayGoals + " " + away.getName());
    }

    /**
     * Displays the final penalty shootout result.
     *
     * @param home home team
     * @param away away team
     * @param homePens number of penalties scored by the home team
     * @param awayPens number of penalties scored by the away team
     */
    public void printPenaltyFinalScore(
            Team home,
            Team away,
            int homePens,
            int awayPens) {

        printMessage("FINAL SCORE:");
        printMessage(home.getName()
                + "(" + homePens + ") "
                + home.getGoalsFor()
                + " - "
                + away.getGoalsFor()
                + " (" + awayPens + ")"
                + away.getName());
    }

    /**
     * Displays the historical Grand Championship podium results.
     *
     * <p>If no results have been recorded, an appropriate message is displayed
     * instead.</p>
     *
     * @param history historical Grand Championship podium results
     */
    public void printPodium(List<GrandChampionPodium> history) {

        printMessage("\n========================");
        printMessage("       HALL OF FAME");
        printMessage("========================\n");

        if (history.isEmpty()) {
            printMessage("No Grand Champions recorded.");
            newLine();
            return;
        }

        for (GrandChampionPodium podium : history) {

            printMessage("Season: " + podium.season());
            printMessage(
                    "1st: " + podium.winner() +
                            " (" + podium.winnerRating() + ")"
            );
            printMessage(
                    "2nd: " + podium.runnerUp() +
                            " (" + podium.runnerUpRating() + ")"
            );
            printMessage(
                    "3rd: " + podium.thirdPlace() +
                            " (" + podium.thirdPlaceRating() + ")"
            );
            newLine();
        }
    }

    /**
     * Displays the in-game save and career management menu.
     */
    public void printMainMenu() {

        printMessage("\n========================");
        printMessage("1 - Continue");
        printMessage("2 - Save Game");
        printMessage("3 - Load Game");
        printMessage("4 - Delete Game");
        printMessage("5 - View Hall of Fame");
        printMessage("6 - Exit");
        printMessage("========================");
    }

    /**
     * Displays the available saved games.
     *
     * <p>If no saves are available, an appropriate message is displayed.</p>
     *
     * @param saves available save files
     */
    public void printGameSaves(File[] saves) {

        printMessage("Available Saves:\n");

        if (saves.length == 0) {
            printMessage("No saves available.");
            return;
        }
        for (int i = 0; i < saves.length; i++) {

            String saveName = saves[i]
                    .getName()
                    .replace(".json", "");

            printMessage((i + 1) + " - " + saveName);
        }

        printMessage("\nSelect save:");
    }

    /**
     * Displays the winner of a penalty shootout.
     *
     * @param team team that won the shootout
     */
    public void penaltyWinner(Team team) {
        printMessage(team.getName() + " WIN");
    }
    public void printGrandChampTitle() {
        printMessage("\n======================");
        printMessage("GRAND CHAMPIONSHIP!");
        printMessage("======================\n");
    }

    /**
     * Prints a blank line to the console.
     */
    public void newLine() {
        System.out.println();
    }

    /**
     * Pauses the simulation when cinematic mode is enabled.
     *
     * <p>If cinematic mode is disabled, the method returns immediately.
     * Interrupted exceptions are handled by restoring the thread interrupt flag.</p>
     *
     * @param settings simulation display settings
     * @param milliseconds delay duration
     */
    public void pause(GameSettings settings, long milliseconds) {

        if (!settings.isCinematicMode()) {
            return;
        }

        try {
            Thread.sleep(milliseconds);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private String formatDecimal(float value) {

        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }
    private String centre(String value) {

        int padding = 5 - value.length();

        int left = padding / 2;
        int right = padding - left;

        return " ".repeat(left) + value + " ".repeat(right);
    }
}
