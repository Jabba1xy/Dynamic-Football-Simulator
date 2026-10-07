package domain.model;

import config.GameConstants;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import service.management.LeagueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a league tier containing a collection of teams.
 *
 * <p>A League manages the teams belonging to a specific tier and applies
 * tier-based modifiers such as league buffs. It also provides functionality
 * for adding and removing teams during promotion and relegation.</p>
 *
 * <p>The supplied {@link SimulationType} determines which simulation-specific
 * rating rules are applied when team ratings are updated.</p>
 *
 * <p>Season simulation is handled separately by {@link Season}, while league
 * movement between tiers is managed by {@link LeagueManager}.</p>
 */
public class League {
    private final LeagueTier tier;
    private final List<Team> teams;

    /**
     * Creates a league containing the supplied teams.
     *
     * <p>The simulation type determines which simulation-specific rating rules
     * are applied when the league is initialised.</p>
     *
     * @param teams teams belonging to the league
     * @param tier tier assigned to the league
     * @param type simulation type controlling rating behaviour
     */
    public League(List<Team> teams, LeagueTier tier, SimulationType type) {
        this.tier = tier;
        this.teams = new ArrayList<>(teams);
        updateTeams(type);
    }
    public LeagueTier getTier() {
        return tier;
    }

    public List<Team> getTeams() {
        return teams;
    }
    public int getLeagueSize() { return teams.size(); }

    // TEAM MANAGEMENT
    public void addTeams(List<Team> newTeams) {
        teams.addAll(newTeams);
    }
    public Team removeTopTeam() {
        return teams.remove(0);
    }
    public Team removeBottomTeam() {
        return teams.remove(teams.size() - 1);
    }
    public int size() {
        return teams.size();
    }

    /**
     * Updates the configuration and rating state of every team in the league.
     *
     * <p>Each team receives the league tier's rating modifier. Teams that have
     * already played matches have their rating recalculated using the dynamic
     * rating equation. The simulation type determines which rating rules are
     * applied during the calculation.</p>
     *
     * <p>Season-specific statistics are reset after the team's rating has been
     * updated.</p>
     *
     * @param type simulation type used to determine rating behaviour
     */
    public void updateTeams(SimulationType type) {

        for (Team team : teams) {
            team.setLeagueBuff(tier.getLeagueBuff());
            if (team.getPlayed() > 0) {
                team.calculateRating(type, getLeagueSize() <= GameConstants.GROUP_STAGE_SIZE);
            }
            team.resetStats();
        }
    }
}
