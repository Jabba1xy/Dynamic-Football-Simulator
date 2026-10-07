package domain.model;

import config.GameConstants;
import domain.enums.MatchOutcome;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchHistoryTest {

    @Test
    void emptyHistoryHasZeroWinPercentage() {

        MatchHistory history = new MatchHistory();

        assertEquals(0, history.getWinPercentage());
    }

    @Test
    void emptyHistoryHasZeroDrawPercentage() {
        MatchHistory history = new MatchHistory();

        assertEquals(0, history.getDrawPercentage());
    }

    @Test
    void oneWinHas100PercentWinPercentage() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.WIN);

        assertEquals(100.0, history.getWinPercentage());
    }

    @Test
    void oneDrawHas100PercentDrawPercentage() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.DRAW);

        assertEquals(100.0, history.getDrawPercentage());
    }

    @Test
    void oneLossHasZeroWinPercentage() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.LOSS);

        assertEquals(0.0, history.getWinPercentage());
    }

    @Test
    void mixedResultsCalculateCorrectWinPercentage() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.WIN);
        history.addResult(MatchOutcome.WIN);
        history.addResult(MatchOutcome.DRAW);
        history.addResult(MatchOutcome.LOSS);

        assertEquals(50.0, history.getWinPercentage());
    }

    @Test
    void mixedResultsCalculateCorrectDrawPercentage() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.WIN);
        history.addResult(MatchOutcome.WIN);
        history.addResult(MatchOutcome.DRAW);
        history.addResult(MatchOutcome.LOSS);

        assertEquals(25.0, history.getDrawPercentage());
    }

    @Test
    void getHistoryReturnsStoredResults() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.WIN);
        history.addResult(MatchOutcome.DRAW);
        history.addResult(MatchOutcome.LOSS);

        assertEquals(List.of(MatchOutcome.WIN,
                        MatchOutcome.DRAW,
                        MatchOutcome.LOSS),
                history.getHistory());
    }

    @Test
    void historyRemovesOldestResultWhenMaximumSizeIsReached() {
        MatchHistory history = new MatchHistory();

        for (int i = 0; i < GameConstants.MAX_HISTORY; i++) {
            history.addResult(MatchOutcome.WIN);
        }

        history.addResult(MatchOutcome.LOSS);

        List<MatchOutcome> results = history.getHistory();

        assertEquals(GameConstants.MAX_HISTORY, results.size());
        assertEquals(MatchOutcome.LOSS, results.get(results.size() - 1));
    }

    @Test
    void historyRemovesOldestResultsWhenMaximumSizeIsExceeded() {
        MatchHistory history = new MatchHistory();

        for (int i = 0; i < 5; i++) {
            history.addResult(MatchOutcome.WIN);
        }

        for (int i = 0; i < GameConstants.MAX_HISTORY; i++) {
            history.addResult(MatchOutcome.LOSS);
        }

        List<MatchOutcome> results = history.getHistory();

        assertEquals(GameConstants.MAX_HISTORY, results.size());
        assertEquals(MatchOutcome.LOSS, results.get(0));
    }

    @Test
    void restoreHistoryAddsToExistingHistory() {
        MatchHistory history = new MatchHistory();

        history.addResult(MatchOutcome.WIN);

        history.restoreHistory(List.of(MatchOutcome.DRAW, MatchOutcome.LOSS));

        assertEquals(List.of(MatchOutcome.WIN,
                        MatchOutcome.DRAW, MatchOutcome.LOSS), history.getHistory());
    }
}