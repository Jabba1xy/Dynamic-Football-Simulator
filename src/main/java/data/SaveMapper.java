package data;

import domain.dto.GrandChampionPodium;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import domain.model.League;
import domain.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts domain objects to save-data records and restores domain objects
 * from persisted save data.
 *
 * <p>SaveMapper separates persistence data structures from the domain model,
 * allowing {@link League} and {@link Team} objects to be converted into dedicated
 * save-data records without directly exposing their internal state to the
 * persistence layer.</p>
 */
public class SaveMapper {

    /**
     * Creates save data for a table league career.
     *
     * <p>The current Champion, Diamond, and Platinum leagues are converted
     * into save-data records along with the Grand Championship history.</p>
     *
     * @param champion the current Champion league
     * @param diamond the current Diamond league
     * @param platinum the current Platinum league
     * @param history historical Grand Championship podium results
     * @return save data representing the current table league career
     */
    public TableLeagueSaveData createSaveData(
            League champion,
            League diamond,
            League platinum,
            List<GrandChampionPodium> history
    ) {
        return new TableLeagueSaveData(
                createLeagueSaveData(champion),
                createLeagueSaveData(diamond),
                createLeagueSaveData(platinum),
                history
        );
    }

    /**
     * Converts a league into its save-data representation.
     *
     * @param league the league to convert
     * @return save data containing the league tier and team data
     */
    private LeagueSaveData createLeagueSaveData(League league) {

        List<TeamSaveData> teams =
                league.getTeams()
                        .stream()
                        .map(this::createTeamSaveData)
                        .toList();

        return new LeagueSaveData(
                league.getTier(),
                teams
        );
    }

    /**
     * Creates save data for a World Cup simulation.
     *
     * <p>The supplied teams and Grand Championship history are converted
     * into their corresponding save-data representations.</p>
     *
     * @param teams teams participating in the World Cup simulation
     * @param history historical Grand Championship podium results
     * @return save data representing the current World Cup simulation
     */
    public WorldCupSaveData createWorldCupSaveData(
            List<Team> teams,
            List<GrandChampionPodium> history
    ) {
        List<TeamSaveData> teamData =
                teams.stream()
                        .map(this::createTeamSaveData)
                        .toList();

        return new WorldCupSaveData(
                teamData,
                history
        );
    }

    /**
     * Converts a team into its save-data representation.
     *
     * @param team the team to convert
     * @return save data containing the team's current state
     */
    private TeamSaveData createTeamSaveData(Team team) {

        return new TeamSaveData(
                team.getName(),
                team.getRating(),
                team.getTotalGoalsFor(),
                team.getTotalGoalsAgainst(),
                team.getTotalPlayed(),
                team.getTotalWins(),
                team.getTotalDraws(),
                team.getTotalLosses(),
                team.getPositionBuff(),
                team.getRatingBuff(),
                team.getTeamStrengthBuff(),
                team.isWorldChampion(),
                team.getRecentForm()
        );
    }

    /**
     * Restores a league from its persisted save data.
     *
     * @param saveData saved league data containing the tier and team states
     * @param type simulation type the restored league belongs to
     * @return a restored {@link League} containing the saved teams
     */
    public League restoreLeague(LeagueSaveData saveData, SimulationType type) {

        List<Team> teams =
                saveData.teams()
                        .stream()
                        .map(this::restoreTeam)
                        .toList();

        return new League(
                new ArrayList<>(teams),
                saveData.tier(),
                type
        );
    }

    /**
     * Restores a World Cup simulation from its persisted save data.
     *
     * <p>The restored teams are placed into a temporary qualifiers league
     * so that the World Cup simulation can continue using the existing
     * league infrastructure.</p>
     *
     * @param saveData saved World Cup data containing team states
     * @return a restored World Cup league containing the saved teams
     */
    public League restoreWorldCup(WorldCupSaveData saveData) {

        List<Team> teams =
                saveData.teams()
                        .stream()
                        .map(this::restoreTeam)
                        .toList();

        return new League(
                new ArrayList<>(teams),
                LeagueTier.QUALIFIERS,
                SimulationType.WORLD_CUP
        );
    }
    private Team restoreTeam(TeamSaveData save) {

        Team team = new Team(save.name());

        team.restore(save);

        return team;
    }
}