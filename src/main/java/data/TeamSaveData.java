package data;

import domain.enums.MatchOutcome;

import java.util.List;

/**
 * Stores the persisted state of a team.
 *
 * <p>Contains the team's current rating, career statistics, rating modifiers,
 * championship status, and recent match history required to restore the team
 * after loading a save.</p>
 *
 * @param name the team's name
 * @param rating the team's current rating
 * @param totalGoalsFor the team's total goals scored
 * @param totalGoalAgainst the team's total goals conceded
 * @param totalPlayed the team's total matches played
 * @param totalWins the team's total wins
 * @param totalDraws the team's total draws
 * @param totalLosses the team's total losses
 * @param positionBuff the team's position bonus
 * @param ratingBuff the team's accumulated rating bonus
 * @param teamStrength the team's strength multiplier
 * @param grandChampion whether the team is the current Grand Champion
 * @param recentForm the team's recent match results
 */
public record TeamSaveData(
        String name,
        int rating,
        int totalGoalsFor,
        int totalGoalAgainst,

        int totalPlayed,
        int totalWins,
        int totalDraws,
        int totalLosses,

        int positionBuff,
        int ratingBuff,
        float teamStrength,
        boolean grandChampion,
        List<MatchOutcome> recentForm
) {}