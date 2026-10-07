package domain.model;

import domain.enums.MatchOutcome;
import config.GameConstants;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Stores a team's recent match performances.
 *
 * <p>The history uses a rolling window system, keeping only the number of
 * recent matches defined by {@link GameConstants#MAX_HISTORY}. This allows
 * the rating system to reflect current form instead of relying only on
 * historical career statistics.</p>
 */
public class MatchHistory {

    private final Deque<MatchOutcome> history = new ArrayDeque<>();

    /**
     * Adds a new match result to the history.
     *
     * <p>If the maximum history size has been reached, the oldest result
     * is removed before adding the new result.</p>
     *
     * @param result the outcome of the completed match
     */
    public void addResult(MatchOutcome result) {

        if (history.size() >= GameConstants.MAX_HISTORY) {
            history.removeFirst();
        }

        history.addLast(result);
    }

    /**
     * Calculates the team's recent win percentage.
     *
     * @return percentage of wins from stored match history
     */
    public double getWinPercentage() {

        if (history.isEmpty()) {
            return 0;
        }

        long wins = history.stream()
                .filter(result -> result == MatchOutcome.WIN)
                .count();

        return (double) wins / history.size() * 100;
    }

    /**
     * Calculates the team's recent draw percentage.
     *
     * @return percentage of draws from stored match history
     */
    public double getDrawPercentage() {

        if (history.isEmpty()) {
            return 0;
        }

        long draws = history.stream()
                .filter(result -> result == MatchOutcome.DRAW)
                .count();

        return (double) draws / history.size() * 100;
    }

    /**
     * Returns a copy of the stored match history.
     *
     * <p>A new list is returned so callers cannot directly modify the internal
     * history collection.</p>
     *
     * @return a list containing the team's recent match outcomes
     */
    public List<MatchOutcome> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * Restores match history from previously saved data.
     *
     * <p>Each result is added through {@link #addResult(MatchOutcome)}, ensuring
     * that the maximum history size is still respected.</p>
     *
     * @param history previously saved match outcomes
     */
    public void restoreHistory(List<MatchOutcome> history)
    {
        for (MatchOutcome result : history) {
            addResult(result);
        }
    }
}