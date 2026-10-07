package domain.model;

import config.GameConstants;
import domain.enums.MatchOutcome;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamTest {

    @Test
    void newTeamStartsWithMinimumRating() {

        Team team = new Team("England");

        assertEquals(GameConstants.MINIMUM_RATING, team.getRating());
    }
    @Test
    void recordWinUpdatesStatsCorrectly() {

        Team team = new Team("Team");

        team.recordWin(3, 1, true);

        assertEquals(3, team.getPoints());
        assertEquals(1, team.getPlayed());
        assertEquals(1, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(0, team.getLosses());

        assertEquals(3, team.getGoalsFor());
        assertEquals(1, team.getGoalsAgainst());

        assertEquals(1, team.getTotalPlayed());
        assertEquals(1, team.getTotalWins());
        assertEquals(0, team.getTotalDraws());
        assertEquals(0, team.getTotalLosses());

        assertEquals(3, team.getTotalGoalsFor());
        assertEquals(1, team.getTotalGoalsAgainst());

        assertEquals(List.of(MatchOutcome.WIN), team.getRecentForm());
    }
    @Test
    void recordDrawUpdatesStatsCorrectly() {

        Team team = new Team("Team");

        team.recordDraw(2, 2, true);

        assertEquals(1, team.getPoints());
        assertEquals(1, team.getPlayed());
        assertEquals(0, team.getWins());
        assertEquals(1, team.getDraws());
        assertEquals(0, team.getLosses());

        assertEquals(2, team.getGoalsFor());
        assertEquals(2, team.getGoalsAgainst());

        assertEquals(1, team.getTotalPlayed());
        assertEquals(0, team.getTotalWins());
        assertEquals(1, team.getTotalDraws());
        assertEquals(0, team.getTotalLosses());

        assertEquals(2, team.getTotalGoalsFor());
        assertEquals(2, team.getTotalGoalsAgainst());

        assertEquals(List.of(MatchOutcome.DRAW), team.getRecentForm());
    }
    @Test
    void recordLossUpdatesStatsCorrectly() {

        Team team = new Team("Team");

        team.recordLoss(0, 2, true);

        assertEquals(0, team.getPoints());
        assertEquals(1, team.getPlayed());
        assertEquals(0, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(1, team.getLosses());

        assertEquals(0, team.getGoalsFor());
        assertEquals(2, team.getGoalsAgainst());

        assertEquals(1, team.getTotalPlayed());
        assertEquals(0, team.getTotalWins());
        assertEquals(0, team.getTotalDraws());
        assertEquals(1, team.getTotalLosses());

        assertEquals(0, team.getTotalGoalsFor());
        assertEquals(2, team.getTotalGoalsAgainst());

        assertEquals(List.of(MatchOutcome.LOSS), team.getRecentForm());
    }
    @Test
    void recordWinDoesNotUpdateLongTermStatsWhenCalculateTotalIsFalse() {

        Team team = new Team("Team");

        team.recordWin(3, 1, false);

        assertEquals(1, team.getPlayed());
        assertEquals(1, team.getWins());
        assertEquals(3, team.getGoalsFor());
        assertEquals(1, team.getGoalsAgainst());
        assertEquals(3, team.getPoints());

        assertEquals(0, team.getTotalPlayed());
        assertEquals(0, team.getTotalWins());
        assertEquals(0, team.getTotalGoalsFor());
        assertEquals(0, team.getTotalGoalsAgainst());

        assertEquals(List.of(), team.getRecentForm());
    }
    @Test
    void recordDrawDoesNotUpdateLongTermStatsWhenCalculateTotalIsFalse() {

        Team team = new Team("Team");

        team.recordDraw(2, 2, false);

        assertEquals(1, team.getPlayed());
        assertEquals(1, team.getDraws());
        assertEquals(1, team.getPoints());

        assertEquals(0, team.getTotalPlayed());
        assertEquals(0, team.getTotalDraws());
        assertEquals(0, team.getTotalGoalsFor());
        assertEquals(0, team.getTotalGoalsAgainst());

        assertEquals(List.of(), team.getRecentForm());
    }
    @Test
    void recordLossDoesNotUpdateLongTermStatsWhenCalculateTotalIsFalse() {

        Team team = new Team("Team");

        team.recordLoss(0, 2, false);

        assertEquals(1, team.getPlayed());
        assertEquals(1, team.getLosses());

        assertEquals(0, team.getTotalPlayed());
        assertEquals(0, team.getTotalLosses());
        assertEquals(0, team.getTotalGoalsFor());
        assertEquals(0, team.getTotalGoalsAgainst());

        assertEquals(List.of(), team.getRecentForm());
    }
    @Test
    void addRatingBuffUpdatesRatingBuff() {

        Team team = new Team("Team");

        team.addRatingBuff(20);
        team.addRatingBuff(10);

        assertEquals(30, team.getRatingBuff());
    }
    @Test
    void resetStatsClearsSeasonStats() {

        Team team = new Team("Team");

        team.recordWin(3, 1, true);
        team.recordDraw(2, 2, true);
        team.recordLoss(0, 2, true);

        team.resetStats();

        // Season statistics should be reset
        assertEquals(0, team.getPoints());
        assertEquals(0, team.getPlayed());
        assertEquals(0, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(0, team.getLosses());
        assertEquals(0, team.getGoalsFor());
        assertEquals(0, team.getGoalsAgainst());

        // Long-term statistics should remain
        assertEquals(3, team.getTotalPlayed());
        assertEquals(1, team.getTotalWins());
        assertEquals(1, team.getTotalDraws());
        assertEquals(1, team.getTotalLosses());
        assertEquals(5, team.getTotalGoalsFor());
        assertEquals(5, team.getTotalGoalsAgainst());
    }
}
