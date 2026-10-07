package domain.dto;

import domain.model.Team;

/**
 * Stores the final positions from a World Cup group stage.
 *
 * <p>Contains the teams that finished first, second, and third
 * within a group after the group stage has been completed.</p>
 *
 * @param winner the team that finished first in the group
 * @param runnerUp the team that finished second in the group
 * @param thirdPlace the team that finished third in the group
 */
public record GroupResult(Team winner, Team runnerUp, Team thirdPlace) {

}