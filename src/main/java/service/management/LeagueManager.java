package service.management;

import domain.dto.GroupResult;
import domain.dto.LeagueResult;
import domain.enums.SimulationType;
import domain.model.League;
import domain.enums.LeagueTier;
import domain.model.Team;
import config.GameConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages league progression between tiers.
 *
 * <p>This class handles promotion and relegation after a completed season,
 * moving teams between leagues based on their final positions. The Champion
 * tier has special rules where top-performing teams qualify for the Grand
 * Championship while lower teams are relegated.</p>
 */
public class LeagueManager {

    /**
     * Determines which teams should be promoted and relegated after a season.
     *
     * <p>Teams are selected based on their final league positions. The champion
     * league has different promotion rules because top-performing teams advance
     * to the Grand Champion tier.</p>
     *
     * @param league the completed league season
     * @return a {@link LeagueResult} containing promoted and relegated teams
     */
    public LeagueResult promoteAndRelegate(League league) {

        List<Team> promoted = new ArrayList<>();
        List<Team> relegated = new ArrayList<>();

        if (league.getTier().equals(LeagueTier.CHAMPION)) {

            for (int i = 0; i < GameConstants.LEAGUE_GRAND_CHAMP_SPOTS; i++) {
                promoted.add(league.removeTopTeam());
            }
            for (int i = 0; i < GameConstants.LEAGUE_PROMOTION_SPOTS; i++) {
                relegated.add(league.removeBottomTeam());
            }
        } else {

            for (int i = 0; i < GameConstants.LEAGUE_PROMOTION_SPOTS; i++) {

                promoted.add(league.removeTopTeam());
                relegated.add(league.removeBottomTeam());
            }
        }
        return new LeagueResult(promoted, relegated);
    }

    /**
     * Determines the teams that qualify from a World Cup group.
     *
     * <p>The teams are selected from the group's final standings, with the
     * top three positions being returned as the group winner, runner-up,
     * and third-place team.</p>
     *
     * @param league the completed World Cup group
     * @return a {@link GroupResult} containing the top three teams
     */
    public GroupResult getGroupStageResult(League league) {

        Team winners = league.getTeams().get(0);
        Team runnersUp = league.getTeams().get(1);
        Team thirdPlace = league.getTeams().get(2);

        return new GroupResult(winners, runnersUp, thirdPlace);
    }
    /**
     * Updates a league with new teams and applies championship status.
     *
     * <p>After promotion and relegation changes are made, teams are added back
     * into the league. The winning team is marked as the Grand Champion before
     * the league is refreshed.</p>
     *
     * @param league the league being updated
     * @param teams teams being added to the league
     * @param winner the winning team of the championship event
     * @param type the simulation type controlling team rating behaviour
     */
    public void updateLeague(League league, List<Team> teams, Team winner, SimulationType type) {

        league.addTeams(teams);

        for (Team team : league.getTeams()) {
            team.setWorldChamps(team == winner);

        }
        league.updateTeams(type);
    }

    /**
     * Sorts teams according to their position or league performance.
     *
     * <p>When position comparison is enabled, teams are sorted by their
     * position bonus. Otherwise, teams are ranked by points, goal difference,
     * goals scored, and rating.</p>
     *
     * @param teams teams to sort
     * @param comparePos whether teams should be sorted by position bonus
     */
    public void sortTeams(List<Team> teams, boolean comparePos) {

        if (comparePos) {
            teams.sort(Comparator.comparingInt(Team::getPositionBuff));
        } else {
            teams.sort(
                    Comparator.comparingInt(Team::getPoints)
                            .thenComparingInt(Team::getGoalDifference)
                            .thenComparingInt(Team::getGoalsFor)
                            .thenComparingInt(Team::getRating)
                            .reversed());
        }
    }
}