package ui;

import domain.dto.GrandChampionPodium;
import domain.enums.LeagueTier;
import domain.model.League;
import domain.model.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsolePrinterTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;
    private ConsolePrinter printer;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        printer = new ConsolePrinter();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void printFinalMatchResultDisplaysHomeWin() {

        Team home = new Team("England");
        Team away = new Team("France");

        printer.printFinalMatchResult(
                home,
                away,
                3,
                1
        );
        String result = output.toString();

        assertTrue(result.contains("FINAL SCORE:"));
        assertTrue(result.contains("England"));
        assertTrue(result.contains("France"));
        assertTrue(result.contains("3 - 1"));
        assertTrue(result.contains("England WIN"));
    }
    @Test
    void printFinalMatchResultDisplaysAwayWin() {

        Team home = new Team("England");
        Team away = new Team("France");

        printer.printFinalMatchResult(
                home,
                away,
                1,
                3
        );
        assertTrue(output.toString().contains("France WIN"));
    }
    @Test
    void printFinalMatchResultDisplaysDraw() {

        Team home = new Team("England");
        Team away = new Team("France");

        printer.printFinalMatchResult(
                home,
                away,
                2,
                2
        );
        assertTrue(output.toString().contains("DRAW"));
    }
    @Test
    void printPenaltyShotResultDisplaysGoal() {

        Team team = new Team("England");

        printer.printPenaltyShotResult(team, true);

        assertTrue(output.toString().contains("ENGLAND GOAL!"));
    }
    @Test
    void printPenaltyShotResultDisplaysMiss() {

        Team team = new Team("England");

        printer.printPenaltyShotResult(team, false);

        assertTrue(output.toString().contains("ENGLAND MISSES!"));
    }
    @Test
    void printPodiumDisplaysEmptyHistoryMessage() {

        printer.printPodium(List.of());

        String result = output.toString();

        assertTrue(result.contains("HALL OF FAME"));
        assertTrue(result.contains("No Grand Champions recorded."));
    }
    @Test
    void printPodiumDisplaysGrandChampionHistory() {

        GrandChampionPodium podium =
                new GrandChampionPodium(
                        1,
                        "England",
                        185,
                        "France",
                        180,
                        "Brazil",
                        175
                );

        printer.printPodium(List.of(podium));

        String result = output.toString();

        assertTrue(result.contains("Season: 1"));
        assertTrue(result.contains("1st: England (185)"));
        assertTrue(result.contains("2nd: France (180)"));
        assertTrue(result.contains("3rd: Brazil (175)"));
    }
    @Test
    void printGameSavesDisplaysMessageWhenNoSavesExist() {

        printer.printGameSaves(new File[0]);

        assertTrue(output.toString().contains("No saves available."));
    }
    @Test
    void printGameSavesDisplaysNumberedSaveNames() {

        File[] saves = {
                new File("career.json"),
                new File("world_cup.json")
        };

        printer.printGameSaves(saves);

        String result = output.toString();

        assertTrue(result.contains("1 - career"));
        assertTrue(result.contains("2 - world_cup"));
        assertTrue(result.contains("Select save:"));
    }
    @Test
    void printFixturesDisplaysTeamsAndRatings() {

        Team team1 = new Team("England");
        Team team2 = new Team("France");

        printer.printFixtures(List.of(team1, team2));

        String result = output.toString();

        assertTrue(result.contains("FIXTURES:"));
        assertTrue(result.contains("England"));
        assertTrue(result.contains("France"));
        assertTrue(result.contains("vs"));
    }
    @Test
    void printPenaltyScoreDisplaysCurrentScore() {

        Team home = new Team("England");
        Team away = new Team("France");

        printer.printPenaltyScore(
                home,
                away,
                4,
                3
        );

        assertTrue(output.toString().contains("England 4 - 3 France"));
    }
    @Test
    void penaltyWinnerDisplaysWinningTeam() {

        Team team = new Team("England");

        printer.penaltyWinner(team);

        assertTrue(output.toString().contains("England WIN"));
    }
    @Test
    void printGrandChampionDisplaysWinningTeam() {

        Team team = new Team("England");

        printer.printGrandChampion(team);

        String result = output.toString();

        assertTrue(result.contains("THE GRAND CHAMPION WINNERS ARE"));
        assertTrue(result.contains("ENGLAND"));
    }
    @Test
    void printLeagueHeaderDisplaysLeagueTier() {

        Team team = new Team("England");

        League league = new League(
                List.of(team),
                LeagueTier.CHAMPION,
                domain.enums.SimulationType.LEAGUE_CAREER
        );
        printer.printLeagueHeader(league);

        assertTrue(output.toString().contains("CHAMPION LEAGUE!"));
    }
    @Test
    void printMatchWeekDisplaysWeekNumber() {

        printer.printMatchWeek(10);

        assertTrue(output.toString().contains("MATCH WEEK 10"));
    }
    @Test
    void pauseDoesNotDelayWhenCinematicModeIsDisabled() {

        GameSettings settings = new GameSettings();
        settings.setCinematicMode(false);

        assertDoesNotThrow(() -> printer.pause(settings, 1000));
    }
    @Test
    void printSimulatorMenuDisplaysSimulationOptions() {

        printer.printSimulatorMenu();

        String result = output.toString();

        assertTrue(result.contains("FOOTBALL SIMULATOR"));
        assertTrue(result.contains("League Career"));
        assertTrue(result.contains("World Cup"));
        assertTrue(result.contains("Exit"));
    }
    @Test
    void printMainMenuDisplaysCareerOptions() {

        printer.printMainMenu();

        String result = output.toString();

        assertTrue(result.contains("Continue"));
        assertTrue(result.contains("Save Game"));
        assertTrue(result.contains("Load Game"));
        assertTrue(result.contains("Delete Game"));
        assertTrue(result.contains("Hall of Fame"));
        assertTrue(result.contains("Exit"));
    }
}