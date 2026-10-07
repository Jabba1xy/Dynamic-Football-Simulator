package service.engine;

import config.GameConstants;
import domain.enums.SimulationType;
import domain.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DynamicRatingEquationTest {
    @Test
    void calculatePreservesRatingDuringWorldCupGroupStage() {

        Team team = new Team("Team 1");

        team.recordWin(3, 0, false);

        DynamicRatingEquation equation = new DynamicRatingEquation();

        int originalRating = team.getRating();

        int result = equation.calculate(
                team,
                SimulationType.WORLD_CUP,
                true
        );
        assertEquals(originalRating, result);
    }
    @Test
    void calculateUpdatesRatingForLeagueSimulation() {

        Team team = new Team("Team 1");

        team.recordWin(3, 0, true);

        DynamicRatingEquation equation = new DynamicRatingEquation();

        int originalRating = team.getRating();

        int result = equation.calculate(
                team,
                SimulationType.LEAGUE_CAREER,
                false
        );
        assertNotEquals(originalRating, result);
    }
    @Test
    void calculateUsesCorrectRatingBuffForSimulationType() {

        Team team = new Team("Team 1");

        team.setLeagueBuff(1.0f);
        team.setTeamStrengthBuff(2.0f);

        DynamicRatingEquation equation = new DynamicRatingEquation();

        int leagueRating = equation.calculate(
                team,
                SimulationType.LEAGUE_CAREER,
                false
        );

        int worldCupRating = equation.calculate(
                team,
                SimulationType.WORLD_CUP,
                false
        );
        assertNotEquals(leagueRating, worldCupRating);
    }
    @Test
    void calculateAppliesGrandChampionBonus() {

        Team normalTeam = new Team("Normal");
        Team championTeam = new Team("Champion");

        championTeam.setWorldChamps(true);

        DynamicRatingEquation equation = new DynamicRatingEquation();

        int normalRating = equation.calculate(
                normalTeam,
                SimulationType.LEAGUE_CAREER,
                false
        );

        int championRating = equation.calculate(
                championTeam,
                SimulationType.LEAGUE_CAREER,
                false
        );

        assertEquals(GameConstants.GRAND_CHAMP_BUFF,
                championRating - normalRating
        );
    }
    @Test
    void calculateAppliesPositionBuff() {

        Team team = new Team("Team 1");

        DynamicRatingEquation equation = new DynamicRatingEquation();

        team.setPositionBuff(10);

        int ratingWithBuff = equation.calculate(
                team,
                SimulationType.LEAGUE_CAREER,
                false
        );

        team.setPositionBuff(0);

        int ratingWithoutBuff = equation.calculate(
                team,
                SimulationType.LEAGUE_CAREER,
                false
        );
        assertEquals(10, ratingWithBuff - ratingWithoutBuff);
    }
    @Test
    void calculateNeverFallsBelowMinimumRating() {

        Team team = new Team("Team 1");

        team.recordLoss(0, 30, true);

        team.setLeagueBuff(1.0f);
        team.setPositionBuff(0);

        DynamicRatingEquation equation = new DynamicRatingEquation();

        int result = equation.calculate(
                team,
                SimulationType.LEAGUE_CAREER,
                false
        );
        assertEquals(GameConstants.MINIMUM_RATING, result);
    }
}
