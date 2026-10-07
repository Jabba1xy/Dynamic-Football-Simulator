package domain.model;

import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LeagueTest {

    @Test
    void constructorStoresTierAndTeams() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");

        List<Team> teams = new ArrayList<>(List.of(team1, team2));

        League league = new League(
                teams,
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        assertEquals(LeagueTier.DIAMOND, league.getTier());
        assertEquals(teams, league.getTeams());
    }
    @Test
    void getLeagueSizeReturnsCorrectSize() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        League league = new League(
                List.of(team1, team2, team3),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        assertEquals(3, league.getLeagueSize());
    }
    @Test
    void addTeamsAddsTeamsToLeague() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        League league = new League(
                List.of(team1, team2),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        league.addTeams(List.of(team3));

        assertEquals(List.of(team1, team2, team3), league.getTeams());

        assertEquals(3, league.getLeagueSize());
    }
    @Test
    void removeTopTeamRemovesAndReturnsFirstTeam() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        League league = new League(
                List.of(team1, team2, team3),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        Team removedTeam = league.removeTopTeam();

        assertEquals(team1, removedTeam);

        assertEquals(List.of(team2, team3), league.getTeams());

        assertEquals(2, league.getLeagueSize());
    }
    @Test
    void removeBottomTeamRemovesAndReturnsLastTeam() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        League league = new League(
                List.of(team1, team2, team3),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        Team removedTeam = league.removeBottomTeam();

        assertEquals(team3, removedTeam);

        assertEquals(List.of(team1, team2), league.getTeams());

        assertEquals(2, league.getLeagueSize());
    }
    @Test
    void updateTeamsAppliesLeagueBuffToAllTeams() {

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");

        League league = new League(
                List.of(team1, team2),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        league.updateTeams(SimulationType.LEAGUE_CAREER);

        assertEquals(LeagueTier.DIAMOND.getLeagueBuff(), team1.getLeagueBuff());

        assertEquals(LeagueTier.DIAMOND.getLeagueBuff(), team2.getLeagueBuff());
    }
    @Test
    void updateTeamsResetsSeasonStats() {

        Team team = new Team("Team 1");

        team.recordWin(3, 1, true);
        team.recordDraw(2, 2, true);
        team.recordLoss(0, 1, true);

        League league = new League(
                List.of(team),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        league.updateTeams(SimulationType.LEAGUE_CAREER);

        assertEquals(0, team.getPlayed());
        assertEquals(0, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(0, team.getLosses());
        assertEquals(0, team.getGoalsFor());
        assertEquals(0, team.getGoalsAgainst());
        assertEquals(0, team.getPoints());
    }
    @Test
    void updateTeamsRecalculatesRatingForTeamsThatHavePlayed() {

        Team team = new Team("Team 1");

        team.recordWin(2, 0, true);

        int ratingBeforeUpdate = team.getRating();

        team.calculateRating(SimulationType.LEAGUE_CAREER, false);

        League league = new League(
                List.of(team),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );
        league.updateTeams(SimulationType.LEAGUE_CAREER);

        assertNotEquals(ratingBeforeUpdate, team.getRating());
    }
}
