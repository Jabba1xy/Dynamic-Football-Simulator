package service.engine;

import domain.enums.SimulationType;
import domain.model.League;
import domain.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TeamFactoryTest {

    @Test
    void createDefaultTeamsLoadsLeagueTeams() {

        TeamFactory factory = new TeamFactory();

        List<Team> teams =
                factory.createDefaultTeams(SimulationType.LEAGUE_CAREER);

        assertFalse(teams.isEmpty());

        assertTrue(teams.stream().allMatch(Objects::nonNull));
    }
    @Test
    void createWorldCupGroupsCreatesTwelveGroupsOfFour() {

        TeamFactory factory = new TeamFactory();

        List<Team> teams = new ArrayList<>();

        for (int i = 1; i <= 48; i++) {
            teams.add(new Team("Team " + i));
        }

        List<League> groups = factory.createWorldCupGroups(teams);

        assertEquals(12, groups.size());

        for (League group : groups) {
            assertEquals(4, group.getTeams().size());
        }
    }
    @Test
    void createWorldCupGroupsThrowsExceptionForInvalidTeamCount() {

        TeamFactory factory = new TeamFactory();

        List<Team> teams = new ArrayList<>();

        for (int i = 1; i <= 47; i++) {
            teams.add(new Team("Team " + i));
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createWorldCupGroups(teams));
    }
}
