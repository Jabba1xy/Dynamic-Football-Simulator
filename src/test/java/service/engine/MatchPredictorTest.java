package service.engine;

import domain.model.Team;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchPredictorTest {

    @Test
    void predictReturnsThreeOutcomeProbabilities() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchPredictor predictor = new MatchPredictor(home, away, new Random());

        double[] probabilities = predictor.predict();

        assertEquals(3, probabilities.length);

        assertTrue(probabilities[0] >= 0);
        assertTrue(probabilities[1] >= 0);
        assertTrue(probabilities[2] >= 0);
    }
    @Test
    void predictPercentagesAddUpToOneHundred() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchPredictor predictor = new MatchPredictor(home, away, new Random());

        double[] probabilities = predictor.predict();

        assertEquals(
                100.0,
                probabilities[0] + probabilities[1] + probabilities[2],
                0.0001
        );
    }
    @Test
    void predictReturnsValidPercentages() {

        Team home = new Team("Home");
        Team away = new Team("Away");

        MatchPredictor predictor = new MatchPredictor(home, away, new Random());

        double[] probabilities = predictor.predict();

        for (double probability : probabilities) {
            assertTrue(probability >= 0.0);
            assertTrue(probability <= 100.0);
        }
    }
}
