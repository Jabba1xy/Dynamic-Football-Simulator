package domain.dto;

import domain.model.Team;

/**
 * Represents the final standings of a Grand Championship knockout tournament.
 *
 * <p>A knockout result records the teams that finished first, second,
 * and third after the tournament and third-place playoff have concluded.</p>
 *
 * @param champion the Grand Champion (1st place)
 * @param runnerUp the tournament runner-up (2nd place)
 * @param thirdPlace the winner of the third-place playoff (3rd place)
 */
public record KnockoutResult(
        Team champion,
        Team runnerUp,
        Team thirdPlace
) {
}