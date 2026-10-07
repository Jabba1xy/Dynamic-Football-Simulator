package service.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import domain.model.League;
import domain.model.Team;
import data.TeamData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory responsible for creating {@link Team} objects.
 *
 * <p>This class loads team data from the application resources and converts
 * the raw data into fully initialised Team domain objects.</p>
 *
 * <p>Team creation is separated from the simulation logic to keep classes
 * focused on a single responsibility. The factory handles data loading,
 * while {@link Team} manages team state during the simulation.</p>
 */
public class TeamFactory {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates the default teams used by the selected simulation.
     *
     * <p>Team data is loaded from the appropriate JSON resource based on the
     * supplied {@link SimulationType}. Each team is initialised with its
     * configured strength multiplier before the resulting list is shuffled
     * to prevent teams from always starting in the same order.</p>
     *
     * @param type simulation type determining which team data file is loaded
     * @return a shuffled list of initial teams
     * @throws IllegalArgumentException if no team data is available for the
     *         supplied simulation type
     * @throws IllegalStateException if the required team data file cannot be found
     * @throws RuntimeException if the team data cannot be loaded
     */
    public List<Team> createDefaultTeams(SimulationType type) {

        String file = switch (type) {
            case LEAGUE_CAREER -> "teams/default_league_teams.json";
            case WORLD_CUP -> "teams/default_world_cup_teams.json";
            default -> throw new IllegalArgumentException(
                    "No team data available for " + type
            );
        };

        try (InputStream stream =
                     getClass().getClassLoader().getResourceAsStream(file)) {

            if (stream == null) {
                throw new IllegalStateException(file + " not found");
            }

            List<TeamData> teamData =
                    mapper.readValue(stream, new TypeReference<>() {});

            List<Team> teams = new ArrayList<>();

            for (TeamData data : teamData) {

                Team team = new Team(data.name());

                team.setTeamStrengthBuff(
                        data.strengthMultiplier()
                );

                teams.add(team);
            }

            Collections.shuffle(teams);

            return teams;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load teams.", e);
        }
    }

    /**
     * Creates the World Cup group stage from the supplied teams.
     *
     * <p>The teams are divided into groups of four and assigned to the
     * corresponding {@link LeagueTier} table. The groups are created in
     * alphabetical table order from Table A through Table L.</p>
     *
     * @param teams teams participating in the World Cup group stage
     * @return a list containing the twelve World Cup groups
     */
    public List<League> createWorldCupGroups(List<Team> teams) {

        if (teams.size() != 48) {
            throw new IllegalArgumentException(
                    "World Cup group stage requires exactly 48 teams"
            );
        }

        List<League> groups = new ArrayList<>();

        LeagueTier[] tiers = {
                LeagueTier.TABLE_A,
                LeagueTier.TABLE_B,
                LeagueTier.TABLE_C,
                LeagueTier.TABLE_D,
                LeagueTier.TABLE_E,
                LeagueTier.TABLE_F,
                LeagueTier.TABLE_G,
                LeagueTier.TABLE_H,
                LeagueTier.TABLE_I,
                LeagueTier.TABLE_J,
                LeagueTier.TABLE_K,
                LeagueTier.TABLE_L
        };

        for (int i = 0; i < tiers.length; i++) {

            groups.add(new League(
                    new ArrayList<>(teams.subList(i * 4, (i + 1) * 4)),
                    tiers[i],
                    SimulationType.WORLD_CUP
            ));
        }
        return groups;
    }
}
