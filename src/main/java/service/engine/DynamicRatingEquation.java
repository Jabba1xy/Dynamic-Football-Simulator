package service.engine;

import domain.enums.SimulationType;
import domain.model.Team;
import config.GameConstants;

/**
 * Handles the Dynamic Rating Equation (DRE) to recalculate team ratings
 * dynamically throughout the simulation.
 */
public class DynamicRatingEquation {

    /**
     * Calculates a team's current rating based on recent performance,
     * goal difference, positioning, and historical achievements.
     *
     * <p><strong>Note:</strong> Ratings remain fixed and are returned as-is
     * during the World Cup group stage.</p>
     *
     * @param team         The team to evaluate.
     * @param type         The simulation type (determines whether to use World Cup strength or league multipliers).
     * @param isGroupStage True if the team is currently playing in a World Cup group stage.
     * @return The dynamically calculated rating, or the existing rating if in a group stage.
     */
    public int calculate(Team team, SimulationType type, boolean isGroupStage) {

        if (!isGroupStage) {
            float teamBuff = type == SimulationType.WORLD_CUP ?
                    team.getTeamStrengthBuff() : team.getLeagueBuff();

            int grandChampBuff = team.isWorldChampion() ?
                    GameConstants.GRAND_CHAMP_BUFF : 0;

            double winPercentage =
                    team.getRecentWinPercentage();

            double drawPercentage =
                    team.getRecentDrawPercentage();

            double calculatedRating =
                    (GameConstants.MINIMUM_RATING
                            + (winPercentage * 0.25)
                            + (drawPercentage * 0.125)
                            + team.getTotalGoalDifference()
                            + team.getRatingBuff())
                            * teamBuff
                            + team.getPositionBuff()
                            + grandChampBuff;

            return Math.max(
                    Math.round((GameConstants.MINIMUM_RATING * teamBuff
                            + team.getPositionBuff())), (int) calculatedRating);
        } else {
            return team.getRating();
        }
    }
}