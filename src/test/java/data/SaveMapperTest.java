package data;

import domain.dto.GrandChampionPodium;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import domain.model.League;
import domain.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveMapperTest {

    @Test
    void createSaveDataMapsLeagueData() {

        Team championTeam = new Team("Champion");
        Team diamondTeam = new Team("Diamond");
        Team platinumTeam = new Team("Platinum");

        League champion = new League(
                new ArrayList<>(List.of(championTeam)),
                LeagueTier.CHAMPION,
                SimulationType.LEAGUE_CAREER
        );

        League diamond = new League(
                new ArrayList<>(List.of(diamondTeam)),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        League platinum = new League(
                new ArrayList<>(List.of(platinumTeam)),
                LeagueTier.PLATINUM,
                SimulationType.LEAGUE_CAREER
        );

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        1,
                        "Champion",
                        200,
                        "Runner Up",
                        180,
                        "Third Place",
                        170
                );

        SaveMapper mapper = new SaveMapper();

        TableLeagueSaveData result =
                mapper.createSaveData(
                        champion,
                        diamond,
                        platinum,
                        List.of(podium)
                );

        assertEquals(LeagueTier.CHAMPION, result.champion().tier());

        assertEquals(LeagueTier.DIAMOND, result.diamond().tier());

        assertEquals(LeagueTier.PLATINUM, result.platinum().tier());

        assertEquals("Champion", result.champion().teams().get(0).name());

        assertEquals("Diamond", result.diamond().teams().get(0).name());

        assertEquals("Platinum", result.platinum().teams().get(0).name());

        assertEquals(List.of(podium), result.history());
    }
    @Test
    void createWorldCupSaveDataMapsTeamsAndHistory() {

        Team england = new Team("England");
        Team brazil = new Team("Brazil");

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        2,
                        "England",
                        210,
                        "Brazil",
                        190,
                        "France",
                        185
                );

        SaveMapper mapper = new SaveMapper();

        WorldCupSaveData result =
                mapper.createWorldCupSaveData(
                        List.of(england, brazil),
                        List.of(podium)
                );

        assertEquals(2, result.teams().size());

        assertEquals(
                "England",
                result.teams().get(0).name()
        );

        assertEquals(
                "Brazil",
                result.teams().get(1).name()
        );

        assertEquals(
                List.of(podium),
                result.history()
        );
    }
    @Test
    void restoreLeagueRestoresTierAndTeams() {

        Team team = new Team("England");

        League league = new League(
                new ArrayList<>(List.of(team)),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        SaveMapper mapper = new SaveMapper();

        TableLeagueSaveData saveData =
                mapper.createSaveData(
                        new League(
                                new ArrayList<>(
                                        List.of(new Team("Champion"))
                                ),
                                LeagueTier.CHAMPION,
                                SimulationType.LEAGUE_CAREER
                        ),
                        league,
                        new League(
                                new ArrayList<>(
                                        List.of(new Team("Platinum"))
                                ),
                                LeagueTier.PLATINUM,
                                SimulationType.LEAGUE_CAREER
                        ),
                        List.of()
                );

        League restored =
                mapper.restoreLeague(
                        saveData.diamond(),
                        SimulationType.LEAGUE_CAREER
                );

        assertEquals(LeagueTier.DIAMOND, restored.getTier());

        assertEquals(1, restored.getTeams().size());

        assertEquals("England", restored.getTeams().get(0).getName());
    }
    @Test
    void restoreWorldCupCreatesQualifiersLeague() {

        TeamSaveData team =
                new TeamSaveData(
                        "England",
                        185,
                        10,
                        5,
                        4,
                        3,
                        1,
                        0,
                        2,
                        10,
                        1.5f,
                        false,
                        List.of()
                );

        WorldCupSaveData saveData =
                new WorldCupSaveData(
                        List.of(team),
                        List.of()
                );

        SaveMapper mapper = new SaveMapper();

        League restored = mapper.restoreWorldCup(saveData);

        assertEquals(LeagueTier.QUALIFIERS, restored.getTier());

        assertEquals(1, restored.getTeams().size());

        assertEquals("England", restored.getTeams().get(0).getName());
    }
    @Test
    void createAndRestoreLeaguePreservesTeamState() {

        Team team = new Team("England");

        team.recordWin(3, 1, true);
        team.recordDraw(2, 2, true);

        team.setPositionBuff(5);
        team.setTeamStrengthBuff(1.5f);
        team.setWorldChamps(true);

        League league = new League(
                new ArrayList<>(List.of(team)),
                LeagueTier.DIAMOND,
                SimulationType.LEAGUE_CAREER
        );

        SaveMapper mapper = new SaveMapper();

        TableLeagueSaveData saveData =
                mapper.createSaveData(
                        new League(
                                new ArrayList<>(
                                        List.of(new Team("Champion"))
                                ),
                                LeagueTier.CHAMPION,
                                SimulationType.LEAGUE_CAREER
                        ),
                        league,
                        new League(
                                new ArrayList<>(
                                        List.of(new Team("Platinum"))
                                ),
                                LeagueTier.PLATINUM,
                                SimulationType.LEAGUE_CAREER
                        ),
                        List.of()
                );

        League restored = mapper.restoreLeague(
                saveData.diamond(),
                SimulationType.LEAGUE_CAREER);

        Team restoredTeam = restored.getTeams().get(0);

        assertEquals(team.getName(), restoredTeam.getName());

        assertEquals(team.getRating(), restoredTeam.getRating());

        assertEquals(team.getTotalGoalsFor(), restoredTeam.getTotalGoalsFor());

        assertEquals(team.getTotalGoalsAgainst(), restoredTeam.getTotalGoalsAgainst());

        assertEquals(team.getTotalPlayed(), restoredTeam.getTotalPlayed());

        assertEquals(team.getTotalWins(), restoredTeam.getTotalWins());

        assertEquals(team.getTotalDraws(), restoredTeam.getTotalDraws());

        assertEquals(team.getTotalLosses(), restoredTeam.getTotalLosses());

        assertEquals(team.getPositionBuff(), restoredTeam.getPositionBuff());

        assertEquals(team.getRatingBuff(), restoredTeam.getRatingBuff());

        assertEquals(team.getTeamStrengthBuff(), restoredTeam.getTeamStrengthBuff());

        assertEquals(team.isWorldChampion(), restoredTeam.isWorldChampion());

        assertEquals(team.getRecentForm(), restoredTeam.getRecentForm());
    }
}