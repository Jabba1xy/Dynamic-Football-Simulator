package domain.dto;

import domain.model.Team;

import java.util.List;

/**
 * Stores the outcome of a league transition.
 *
 * <p>Contains the teams promoted and relegated after a league season
 * has finished.</p>
 *
 * @param promoted  teams moving to a higher league tier
 * @param relegated teams moving to a lower league tier
 */
public record LeagueResult(
        List<Team> promoted,
        List<Team> relegated
) {
}