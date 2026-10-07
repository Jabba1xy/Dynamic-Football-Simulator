package service.management;

import domain.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MatchResultTest {

    @Test
    void homeWinAndAwayLossUpdatesBothTeams() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchResult result = new MatchResult(
                home, away, 2, 0
        );

        result.apply(true);

        assertEquals(1, home.getWins());
        assertEquals(0, away.getWins());

        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        assertEquals(2, home.getGoalsFor());
        assertEquals(0, home.getGoalsAgainst());

        assertEquals(0, away.getGoalsFor());
        assertEquals(2, away.getGoalsAgainst());
    }
    @Test
    void awayWinAndHomeLossUpdatesBothTeams() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchResult result = new MatchResult(
                home, away, 0, 2);

        result.apply(true);

        assertEquals(1, away.getWins());
        assertEquals(0, home.getWins());

        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        assertEquals(2, away.getGoalsFor());
        assertEquals(0, away.getGoalsAgainst());

        assertEquals(0, home.getGoalsFor());
        assertEquals(2, home.getGoalsAgainst());
    }
    @Test
    void drawUpdatesBothTeams() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchResult result = new MatchResult(
                home, away, 2, 2);

        result.apply(true);

        assertEquals(1, away.getDraws());
        assertEquals(1, home.getDraws());

        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        assertEquals(2, away.getGoalsFor());
        assertEquals(2, away.getGoalsAgainst());

        assertEquals(2, home.getGoalsFor());
        assertEquals(2, home.getGoalsAgainst());
    }
    @Test
    void calculateTotalFalseDoesNotUpdateLongTermStats() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchResult result = new MatchResult(
                home, away, 2, 0);

        result.apply(false);

        // Current match statistics should still update
        assertEquals(1, home.getWins());
        assertEquals(1, away.getLosses());
        assertEquals(1, home.getPlayed());
        assertEquals(1, away.getPlayed());

        // Long-term statistics should remain unchanged
        assertEquals(0, home.getTotalWins());
        assertEquals(0, away.getTotalWins());

        assertEquals(0, home.getTotalPlayed());
        assertEquals(0, away.getTotalPlayed());

        assertEquals(0, away.getTotalGoalsFor());
        assertEquals(0, away.getTotalGoalsAgainst());

        assertEquals(0, home.getTotalGoalsFor());
        assertEquals(0, home.getTotalGoalsAgainst());
    }
    @Test
    void underdogHomeWinReceivesRatingBuff() {

        Team home = mock(Team.class);
        Team away = mock(Team.class);

        when(home.getRating()).thenReturn(100);
        when(away.getRating()).thenReturn(140);

        MatchResult result = new MatchResult(
                home, away, 2, 0);

        result.apply(true);

        verify(home).addRatingBuff(20);
        verify(away).addRatingBuff(-20);
    }
    @Test
    void underdogAwayWinReceivesRatingBuff() {

        Team home = mock(Team.class);
        Team away = mock(Team.class);

        when(home.getRating()).thenReturn(140);
        when(away.getRating()).thenReturn(100);

        MatchResult result = new MatchResult(
                home, away, 0, 2);

        result.apply(true);

        verify(home).addRatingBuff(-20);
        verify(away).addRatingBuff(20);
    }
    @Test
    void underdogHomeDrawReceivesRatingBuff() {

        Team home = mock(Team.class);
        Team away = mock(Team.class);

        when(home.getRating()).thenReturn(100);
        when(away.getRating()).thenReturn(120);

        MatchResult result = new MatchResult(
                home, away, 1, 1);

        result.apply(true);

        verify(home).addRatingBuff(5);
        verify(away).addRatingBuff(-5);
    }
    @Test
    void underdogAwayDrawReceivesRatingBuff() {

        Team home = mock(Team.class);
        Team away = mock(Team.class);

        when(home.getRating()).thenReturn(120);
        when(away.getRating()).thenReturn(100);

        MatchResult result = new MatchResult(
                home, away, 1, 1);

        result.apply(true);

        verify(home).addRatingBuff(-5);
        verify(away).addRatingBuff(5);
    }
}