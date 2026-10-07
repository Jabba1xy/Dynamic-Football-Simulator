package domain.dto;

import domain.dto.LeagueResult;
import domain.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeagueResultTest {

    @Test
    void getPromotedReturnsPromotedTeams() {
        Team teamOne = new Team("England");
        Team teamTwo = new Team("France");

        List<Team> promoted = List.of(teamOne, teamTwo);
        List<Team> relegated = List.of();

        LeagueResult result = new LeagueResult(promoted, relegated);

        assertEquals(promoted, result.promoted());
    }

    @Test
    void getRelegatedReturnsRelegatedTeams() {
        Team teamOne = new Team("Spain");
        Team teamTwo = new Team("Germany");

        List<Team> promoted = List.of();
        List<Team> relegated = List.of(teamOne, teamTwo);

        LeagueResult result = new LeagueResult(promoted, relegated);

        assertEquals(relegated, result.relegated());
    }

    @Test
    void emptyListsAreReturnedCorrectly() {
        LeagueResult result = new LeagueResult(
                List.of(),
                List.of()
        );
        assertEquals(List.of(), result.promoted());
        assertEquals(List.of(), result.relegated());
    }
}
