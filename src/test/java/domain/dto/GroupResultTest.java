package domain.dto;

import domain.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupResultTest {

    @Test
    void getWinnerReturnsWinner() {
        Team winner = new Team("England");
        Team runnerUp = new Team("France");
        Team thirdPlace = new Team("Spain");

        GroupResult result = new GroupResult(winner, runnerUp, thirdPlace);

        assertEquals(winner, result.winner());
    }

    @Test
    void getRunnerUpReturnsRunnerUp() {
        Team winner = new Team("England");
        Team runnerUp = new Team("France");
        Team thirdPlace = new Team("Spain");

        GroupResult result = new GroupResult(winner, runnerUp, thirdPlace);

        assertEquals(runnerUp, result.runnerUp());
    }

    @Test
    void getThirdPlaceReturnsThirdPlace() {
        Team winner = new Team("England");
        Team runnerUp = new Team("France");
        Team thirdPlace = new Team("Spain");

        GroupResult result = new GroupResult(winner, runnerUp, thirdPlace);

        assertEquals(thirdPlace, result.thirdPlace());
    }
}
