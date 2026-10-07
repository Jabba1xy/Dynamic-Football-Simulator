package data;

import domain.dto.GrandChampionPodium;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the historical results of Grand Championship tournaments.
 *
 * <p>The Hall of Fame is maintained in memory during the simulation and
 * is persisted as part of the save data when the player saves their career.</p>
 */
public class HallOfFame {

    private final List<GrandChampionPodium> history = new ArrayList<>();

    /**
     * Returns the number assigned to the next Grand Championship season.
     *
     * @return the next Grand Championship season number
     */
    public int getSeasonNumber() {

        if (history.isEmpty()) {
            return 1;
        }

        return history.get(history.size() - 1).season() + 1;
    }

    /**
     * Returns the complete Grand Championship history.
     *
     * @return list of historical podium finishes
     */
    public List<GrandChampionPodium> getHistory() {
        return history;
    }

    /**
     * Adds a completed Grand Championship result to the Hall of Fame.
     *
     * @param podium the completed championship podium
     */
    public void saveResult(GrandChampionPodium podium) {
        history.add(podium);
    }

    /**
     * Restores the Hall of Fame from previously saved history.
     *
     * <p>Any existing history is cleared before the saved results are added.</p>
     *
     * @param savedHistory previously saved Grand Championship history
     */
    public void restore(List<GrandChampionPodium> savedHistory) {

        history.clear();

        if (savedHistory != null) {
            history.addAll(savedHistory);
        }
    }
}