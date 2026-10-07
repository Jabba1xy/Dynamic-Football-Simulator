package data;

import domain.enums.LeagueTier;

import java.util.List;

/**
 * Stores the data required to persist a league and its teams.
 *
 * <p>This record represents the save-data form of a {@link domain.model.League},
 * containing its league tier and the saved state of each team.</p>
 *
 * @param tier the league tier associated with the saved league
 * @param teams the saved team data belonging to the league
 */
public record LeagueSaveData(LeagueTier tier, List<TeamSaveData> teams) {

}