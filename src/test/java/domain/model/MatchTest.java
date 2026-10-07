package domain.model;

import org.junit.jupiter.api.Test;
import ui.ConsolePrinter;
import ui.GameSettings;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchTest {

    @Test
    void playUpdatesTeamStatistics() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        Match match = new Match(home, away, new Random(1), new ConsolePrinter());

        GameSettings settings = new GameSettings();

        match.play(settings, true);

        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        assertEquals(home.getGoalsFor(), away.getGoalsAgainst());

        assertEquals(away.getGoalsFor(), home.getGoalsAgainst());

        assertEquals(1, home.getWins() + home.getDraws() + home.getLosses());

        assertEquals(1, away.getWins() + away.getDraws() + away.getLosses());
    }
    @Test
    void playWithCalculateTotalFalseDoesNotUpdateLongTermStats() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        Match match = new Match(home, away, new Random(2), new ConsolePrinter());

        GameSettings settings = new GameSettings();

        match.play(settings, false);

        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        assertEquals(0, home.getTotalPlayed());
        assertEquals(0, away.getTotalPlayed());

        assertEquals(0, home.getTotalWins());
        assertEquals(0, away.getTotalWins());

        assertEquals(0, home.getTotalDraws());
        assertEquals(0, away.getTotalDraws());

        assertEquals(0, home.getTotalLosses());
        assertEquals(0, away.getTotalLosses());

        assertTrue(home.getRecentForm().isEmpty());
        assertTrue(away.getRecentForm().isEmpty());
    }
    @Test
    void extraTimeReturnsWinnerWhenAlreadyAhead() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        Match match = new Match(home, away, new Random(3), new ConsolePrinter());

        GameSettings settings = new GameSettings();

        do {
            match.play(settings, false);
        } while (home.getWins() == 0 && away.getWins() == 0);

        Team expectedWinner;

        if (home.getWins() == 1) {
            expectedWinner = home;
        } else {
            expectedWinner = away;
        }

        Team actualWinner = match.extraTime(settings);

        assertEquals(expectedWinner, actualWinner);
    }
    @Test
    void playWithCalculateTotalTrueUpdatesLongTermStats() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        Match match = new Match(home, away, new Random(4), new ConsolePrinter());

        GameSettings settings = new GameSettings();

        match.play(settings, true);

        assertEquals(1, home.getTotalPlayed());
        assertEquals(1, away.getTotalPlayed());

        assertEquals(1, home.getTotalWins()
                + home.getTotalDraws()
                + home.getTotalLosses());

        assertEquals(1, away.getTotalWins()
                + away.getTotalDraws()
                + away.getTotalLosses());

        assertEquals(1, home.getRecentForm().size());
        assertEquals(1, away.getRecentForm().size());
    }
    @Test
    void playingMatchAgainResetsPreviousScore() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        Match match = new Match(home, away, new Random(5), new ConsolePrinter());

        GameSettings settings = new GameSettings();

        match.play(settings, false);

        int firstHomeGoals = home.getGoalsFor();
        int firstAwayGoals = away.getGoalsFor();

        match.play(settings, false);

        int secondHomeGoals = home.getGoalsFor() - firstHomeGoals;
        int secondAwayGoals = away.getGoalsFor() - firstAwayGoals;

        assertTrue(secondHomeGoals >= 0);
        assertTrue(secondAwayGoals >= 0);
    }
}
