package data;

import domain.dto.GrandChampionPodium;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HallOfFameTest {

    @Test
    void getSeasonNumberStartsAtOneWhenHistoryIsEmpty() {

        HallOfFame hallOfFame = new HallOfFame();

        assertEquals(1, hallOfFame.getSeasonNumber());
    }
    @Test
    void saveResultAddsPodiumAndIncrementsSeasonNumber() {

        HallOfFame hallOfFame = new HallOfFame();

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        1,
                        "Champion",
                        200,
                        "Runner Up",
                        180,
                        "Third Place",
                        170
                );
        hallOfFame.saveResult(podium);

        assertEquals(1, hallOfFame.getHistory().size());
        assertEquals(podium, hallOfFame.getHistory().get(0));
        assertEquals(2, hallOfFame.getSeasonNumber());
    }
    @Test
    void getSeasonNumberUsesLastSavedSeasonNumber() {

        HallOfFame hallOfFame = new HallOfFame();

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        5,
                        "Champion",
                        200,
                        "Runner Up",
                        180,
                        "Third Place",
                        170
                );
        hallOfFame.saveResult(podium);

        assertEquals(6, hallOfFame.getSeasonNumber());
    }
    @Test
    void restoreReplacesExistingHistory() {

        HallOfFame hallOfFame = new HallOfFame();

        GrandChampionPodium oldPodium =
                new GrandChampionPodium(
                        1,
                        "Old Champion",
                        200,
                        "Old Runner Up",
                        180,
                        "Old Third Place",
                        170
                );
        GrandChampionPodium restoredPodium =
                new GrandChampionPodium(
                        7,
                        "New Champion",
                        210,
                        "New Runner Up",
                        190,
                        "New Third Place",
                        175
                );
        hallOfFame.saveResult(oldPodium);
        hallOfFame.restore(List.of(restoredPodium));

        assertEquals(1, hallOfFame.getHistory().size());
        assertEquals(restoredPodium, hallOfFame.getHistory().get(0));
        assertEquals(8, hallOfFame.getSeasonNumber());
    }
    @Test
    void restoreWithNullClearsExistingHistory() {

        HallOfFame hallOfFame = new HallOfFame();

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        1,
                        "Champion",
                        200,
                        "Runner Up",
                        180,
                        "Third Place",
                        170
                );
        hallOfFame.saveResult(podium);
        hallOfFame.restore(null);

        assertTrue(hallOfFame.getHistory().isEmpty());
        assertEquals(1, hallOfFame.getSeasonNumber());
    }
}