package service.management;

import domain.dto.GroupResult;
import domain.dto.LeagueResult;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import domain.model.League;
import domain.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LeagueManagerTest {

    @Test
    void normalLeaguePromotesAndRelegatesCorrectTeams() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");
        Team team4 = new Team("Team 4");
        Team team5 = new Team("Team 5");
        Team team6 = new Team("Team 6");
        Team team7 = new Team("Team 7");
        Team team8 = new Team("Team 8");
        Team team9 = new Team("Team 9");
        Team team10 = new Team("Team 10");

        List<Team> teams = List.of(
                team1, team2, team3, team4, team5,
                team6, team7, team8, team9, team10
        );

        League diamond = new League(
                teams,
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        LeagueManager manager = new LeagueManager();

        LeagueResult result = manager.promoteAndRelegate(diamond);

        assertEquals(
                List.of(team1, team2, team3),
                result.promoted()
        );

        assertEquals(
                List.of(team10, team9, team8),
                result.relegated()
        );

        assertEquals(4, diamond.getTeams().size());
    }

    @Test
    void championLeaguePromotesGrandChampionsAndRelegatesCorrectTeams() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");
        Team team4 = new Team("Team 4");
        Team team5 = new Team("Team 5");
        Team team6 = new Team("Team 6");
        Team team7 = new Team("Team 7");
        Team team8 = new Team("Team 8");
        Team team9 = new Team("Team 9");
        Team team10 = new Team("Team 10");

        List<Team> teams = List.of(
                team1, team2, team3, team4, team5,
                team6, team7, team8, team9, team10
        );

        League champion = new League(
                teams,
                LeagueTier.CHAMPION,
                SimulationType.LEAGUE_CAREER
        );

        LeagueManager manager = new LeagueManager();

        LeagueResult result = manager.promoteAndRelegate(champion);

        assertEquals(
                List.of(team1, team2, team3, team4),
                result.promoted()
        );

        assertEquals(
                List.of(team10, team9, team8),
                result.relegated()
        );

        assertEquals(3, champion.getTeams().size());
    }

    @Test
    void getGroupStageResultReturnsTopThreeTeams() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");
        Team team4 = new Team("Team 4");

        List<Team> teams = List.of(team1, team2, team3, team4);

        League groupA = new League(
                teams,
                LeagueTier.CHAMPION,
                SimulationType.LEAGUE_CAREER
        );

        LeagueManager manager = new LeagueManager();

        GroupResult result = manager.getGroupStageResult(groupA);

        assertEquals(team1, result.winner());

        assertEquals(team2, result.runnerUp());

        assertEquals(team3, result.thirdPlace());
    }

    @Test
    void sortTeamsByPositionBuffWhenComparePosIsTrue() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        List<Team> teams = new ArrayList<>(List.of(team1, team2, team3));

        LeagueManager manager = new LeagueManager();

        team1.setPositionBuff(30);
        team2.setPositionBuff(10);
        team3.setPositionBuff(20);

        manager.sortTeams(teams, true);

        List<Team> result = new ArrayList<>(teams);

        assertEquals(List.of(team2, team3, team1), result);
    }

    @Test
    void sortTeamsByPerformanceWhenComparePosIsFalse() {

        Team team1 = mock(Team.class);
        Team team2 = mock(Team.class);
        Team team3 = mock(Team.class);
        Team team4 = mock(Team.class);

        when(team1.getPoints()).thenReturn(6);
        when(team1.getGoalDifference()).thenReturn(1);
        when(team1.getGoalsFor()).thenReturn(3);
        when(team1.getRating()).thenReturn(100);

        when(team2.getPoints()).thenReturn(6);
        when(team2.getGoalDifference()).thenReturn(2);
        when(team2.getGoalsFor()).thenReturn(3);
        when(team2.getRating()).thenReturn(100);

        when(team3.getPoints()).thenReturn(6);
        when(team3.getGoalDifference()).thenReturn(2);
        when(team3.getGoalsFor()).thenReturn(5);
        when(team3.getRating()).thenReturn(100);

        when(team4.getPoints()).thenReturn(6);
        when(team4.getGoalDifference()).thenReturn(2);
        when(team4.getGoalsFor()).thenReturn(5);
        when(team4.getRating()).thenReturn(120);

        List<Team> teams = new ArrayList<>(
                List.of(team1, team2, team3, team4)
        );

        LeagueManager manager = new LeagueManager();

        manager.sortTeams(teams, false);

        assertEquals(
                List.of(team4, team3, team2, team1),
                teams
        );
    }
    @Test
    void updateLeagueAddsTeamsToLeague() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");

        League league = new League(
                new ArrayList<>(),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        LeagueManager manager = new LeagueManager();

        manager.updateLeague(
                league,
                List.of(team1, team2),
                team1,
                SimulationType.LEAGUE_CAREER
        );

        assertEquals(List.of(team1, team2), league.getTeams());
    }
    @Test
    void updateLeagueMarksOnlyWinnerAsWorldChampion() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        League league = new League(
                new ArrayList<>(),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        LeagueManager manager = new LeagueManager();

        manager.updateLeague(
                league,
                List.of(team1, team2, team3),
                team2,
                SimulationType.LEAGUE_CAREER
        );

        assertFalse(team1.isWorldChampion());
        assertTrue(team2.isWorldChampion());
        assertFalse(team3.isWorldChampion());
    }
}
