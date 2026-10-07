package domain.dto;

/**
 * Represents a historical Grand Championship podium entry.
 *
 * <p>Stores the finishing positions and final ratings from a completed
 * Grand Championship tournament or World Cup so results can be persisted and viewed
 * after the application has been closed.</p>
 *
 * @param season the Grand Championship cycle number
 * @param winner the team that finished first
 * @param winnerRating the winner's final rating
 * @param runnerUp the team that finished second
 * @param runnerUpRating the runner-up's final rating
 * @param thirdPlace the team that finished third
 * @param thirdPlaceRating the third-place team's final rating
 */
public record GrandChampionPodium(
        int season,
        String winner,
        int winnerRating,
        String runnerUp,
        int runnerUpRating,
        String thirdPlace,
        int thirdPlaceRating)
  {
}