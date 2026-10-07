package domain.model;

import org.junit.jupiter.api.Test;
import ui.ConsolePrinter;
import ui.GameSettings;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PenaltyShootoutTest {

    @Test
    void shootoutAlwaysProducesWinner() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        PenaltyShootout shootout = new PenaltyShootout(
                home, away,
                new Random(42),
                new ConsolePrinter());

        GameSettings settings = new GameSettings();

        Team winner = shootout.play(settings);

        assertNotNull(winner);
        assertTrue(winner == home || winner == away);
    }
    @Test
    void sameRandomSeedProducesSameWinner() {

        Team homeOne = new Team("Home");
        Team awayOne = new Team("Away");

        Team homeTwo = new Team("Home");
        Team awayTwo = new Team("Away");

        PenaltyShootout firstShootout = new PenaltyShootout(
                homeOne,
                awayOne,
                new Random(42),
                new ConsolePrinter()
        );
        PenaltyShootout secondShootout = new PenaltyShootout(
                homeTwo,
                awayTwo,
                new Random(42),
                new ConsolePrinter()
        );
        GameSettings settings = new GameSettings();

        Team firstWinner = firstShootout.play(settings);
        Team secondWinner = secondShootout.play(settings);

        assertEquals(firstWinner.getName(), secondWinner.getName());
    }
    @Test
    void shootoutDoesNotModifyTeamStatistics() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        PenaltyShootout shootout = new PenaltyShootout(
                home,
                away,
                new Random(42),
                new ConsolePrinter()
        );

        GameSettings settings = new GameSettings();

        shootout.play(settings);

        assertEquals(0, home.getPlayed());
        assertEquals(0, away.getPlayed());

        assertEquals(0, home.getWins());
        assertEquals(0, away.getWins());

        assertEquals(0, home.getDraws());
        assertEquals(0, away.getDraws());

        assertEquals(0, home.getLosses());
        assertEquals(0, away.getLosses());

        assertEquals(0, home.getGoalsFor());
        assertEquals(0, away.getGoalsFor());

        assertEquals(0, home.getGoalsAgainst());
        assertEquals(0, away.getGoalsAgainst());
    }
}
