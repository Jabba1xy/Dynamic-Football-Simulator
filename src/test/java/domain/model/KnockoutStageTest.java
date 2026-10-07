package domain.model;

import domain.dto.KnockoutResult;
import domain.enums.SimulationType;
import org.junit.jupiter.api.Test;
import ui.ConsolePrinter;
import ui.GameSettings;
import ui.Input;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnockoutStageTest {

    @Test
    void runKnockoutThrowsExceptionForInvalidTeamCount() {
        KnockoutStage stage = new KnockoutStage(new ConsolePrinter(), new TestInput());

        List<Team> teams = List.of(
                new Team("Team 1"),
                new Team("Team 2"),
                new Team("Team 3")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> stage.runKnockout(
                        teams,
                        new GameSettings(),
                        SimulationType.LEAGUE_CAREER));
    }
    @Test
    void runKnockoutProducesCompleteTournamentResult() {

        KnockoutStage stage = new KnockoutStage(new ConsolePrinter(), new TestInput());

        List<Team> teams = new ArrayList<>(List.of(
                new Team("Team 1"),
                new Team("Team 2"),
                new Team("Team 3"),
                new Team("Team 4")
        ));

        KnockoutResult result = stage.runKnockout(
                teams,
                new GameSettings(),
                SimulationType.WORLD_CUP
        );

        assertNotNull(result);
        assertNotNull(result.champion());
        assertNotNull(result.runnerUp());
        assertNotNull(result.thirdPlace());
    }
    @Test
    void runKnockoutReturnsThreeDifferentTeams() {

        KnockoutStage stage = new KnockoutStage(new ConsolePrinter(), new TestInput());

        List<Team> teams = new ArrayList<>(List.of(
                new Team("Team 1"),
                new Team("Team 2"),
                new Team("Team 3"),
                new Team("Team 4")
        ));

        KnockoutResult result = stage.runKnockout(
                teams,
                new GameSettings(),
                SimulationType.WORLD_CUP
        );

        assertNotEquals(result.champion(), result.runnerUp());
        assertNotEquals(result.champion(), result.thirdPlace());
        assertNotEquals(result.runnerUp(), result.thirdPlace());
    }
    @Test
    void resetStatsClearsSeasonStatistics() {

        Team team = new Team("Test Team");

        team.recordWin(3, 1, false);
        team.recordDraw(2, 2, false);
        team.recordLoss(0, 1, false);

        team.resetStats();

        assertEquals(0, team.getPoints());
        assertEquals(0, team.getPlayed());
        assertEquals(0, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(0, team.getLosses());
        assertEquals(0, team.getGoalsFor());
        assertEquals(0, team.getGoalsAgainst());
    }
    @Test
    void worldCupKnockoutDoesNotShuffleTeamsBeforeStarting() {

        KnockoutStage stage = new KnockoutStage(new ConsolePrinter(), new TestInput());

        List<Team> teams = new ArrayList<>(List.of(
                new Team("Team 1"),
                new Team("Team 2"),
                new Team("Team 3"),
                new Team("Team 4")
        ));

        List<Team> originalOrder = new ArrayList<>(teams);

        stage.runKnockout(
                teams,
                new GameSettings(),
                SimulationType.WORLD_CUP
        );
        assertEquals(originalOrder, teams);
    }

    /**
     * Input implementation used to prevent tests from waiting for
     * console input during knockout matches.
     */
    private static class TestInput extends Input {
        @Override
        public void pressEnter() {
            // Do nothing during tests.
        }
    }
}
