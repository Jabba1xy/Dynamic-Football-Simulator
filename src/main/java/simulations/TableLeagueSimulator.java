package simulations;

import config.GameConstants;
import data.*;
import domain.dto.GrandChampionPodium;
import domain.dto.KnockoutResult;
import domain.enums.LeagueTier;
import domain.enums.SimulationType;
import domain.model.KnockoutStage;
import domain.model.League;
import domain.model.Season;
import domain.model.Team;
import ui.ConsolePrinter;
import ui.GameMenu;
import ui.GameSettings;
import ui.Input;
import service.management.LeagueManager;
import domain.dto.LeagueResult;
import service.engine.TeamFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the complete table-based football career simulation.
 *
 * <p>This class acts as the main orchestration layer for the league simulation.
 * It manages league creation, season progression, promotion and relegation,
 * Grand Championship qualification, saving/loading, and user interaction.</p>
 *
 * <p>The simulator operates using a tiered league structure consisting of:
 * Qualifiers, Platinum, Diamond, and Champion leagues. After the initial
 * qualification stage, teams compete across multiple seasons with promotion,
 * relegation, and rating progression.</p>
 *
 * <p>Simulation logic is delegated to dedicated domain and service classes.
 * League matches are handled by {@link Season} and {@link domain.model.Match},
 * league movement is managed through {@link LeagueManager}, and persistence
 * is handled through {@link SaveGame} and {@link SaveMapper}.</p>
 */
public class TableLeagueSimulator {

    private League champion;
    private League diamond;
    private League platinum;
    private KnockoutResult result;
    private final List<League> leagueSystem;
    private final List<Team> teamList;
    private final Season season;
    private final KnockoutStage grandChampKnockouts;
    private final LeagueManager leagueManager;
    private final ConsolePrinter printer;
    private final GameSettings settings;
    private final HallOfFame hallOfFame;
    private final SaveMapper saveMapper;
    private final Input input;
    private final GameMenu<TableLeagueSaveData> menu;

    /**
     * Creates a new table league simulator.
     *
     * <p>Initialises the required simulation services, league management,
     * knockout and season components, persistence systems, and user-interface
     * dependencies. The supplied {@link SimulationType} determines which
     * simulation data and save configuration are used.</p>
     *
     * @param type simulation type used to determine the appropriate team data
     *             and save configuration
     * @param printer handles console output and presentation
     * @param input handles console user input
     */
    public TableLeagueSimulator(
            SimulationType type,
            ConsolePrinter printer,
            Input input) {

        TeamFactory teamFactory = new TeamFactory();
        this.printer = printer;
        this.input = input;

        grandChampKnockouts = new KnockoutStage(printer, input);
        season = new Season(printer, input);

        leagueManager = new LeagueManager();
        leagueSystem = new ArrayList<>();
        settings = new GameSettings();

        hallOfFame = new HallOfFame();
        saveMapper = new SaveMapper();

        SaveGame<TableLeagueSaveData> saveGame =
                new SaveGame<>(type, TableLeagueSaveData.class);

        menu = new GameMenu<>(
                printer,
                saveGame,
                hallOfFame,
                input
        );
        teamList = teamFactory.createDefaultTeams(type);
    }

    /**
     * Starts the table league career simulation.
     *
     * <p>The simulation performs the following stages:</p>
     *
     * <ol>
     *     <li>Displays the main menu and handles loading existing saves.</li>
     *     <li>Creates the initial qualifying league if starting a new career.</li>
     *     <li>Splits teams into Platinum, Diamond, and Champion divisions.</li>
     *     <li>Simulates league seasons across all divisions.</li>
     *     <li>Processes promotion and relegation between leagues.</li>
     *     <li>Runs the Grand Championship knockout tournament.</li>
     *     <li>Saves career progress and repeats until the user exits.</li>
     * </ol>
     */
    public void start() {

        printer.printMessage("!!!DYNAMIC FOOTBALL SIMULATOR!!!");

        TableLeagueSaveData saveData = menu.show(false, null);
        boolean skipQualifiers = saveData != null;

        printer.printMessage("Enable cinematic mode? (yes/no)");

        String answer = input.userInput().trim();
        settings.setCinematicMode(answer.equalsIgnoreCase("yes"));

        if (skipQualifiers) {
            champion = saveMapper.restoreLeague(saveData.champion(), SimulationType.LEAGUE_CAREER);
            diamond = saveMapper.restoreLeague(saveData.diamond(), SimulationType.LEAGUE_CAREER);
            platinum = saveMapper.restoreLeague(saveData.platinum(), SimulationType.LEAGUE_CAREER);

            hallOfFame.restore(saveData.history());
        } else {

            League qualifiers = new League(
                    new ArrayList<>(teamList),
                    LeagueTier.QUALIFIERS,
                    SimulationType.LEAGUE_CAREER);

            leagueSystem.add(qualifiers);

            season.playSeason(leagueSystem, GameConstants.DEFAULT_LEAGUE_SIZE, settings, SimulationType.LEAGUE_CAREER, true);

            champion = new League(
                    new ArrayList<>(qualifiers.getTeams().subList(0,10)),
                    LeagueTier.CHAMPION,
                    SimulationType.LEAGUE_CAREER
            );
            diamond = new League(
                    new ArrayList<>(qualifiers.getTeams().subList(10,20)),
                    LeagueTier.DIAMOND,
                    SimulationType.LEAGUE_CAREER
            );
            platinum = new League(
                    new ArrayList<>(qualifiers.getTeams().subList(20,30)),
                    LeagueTier.PLATINUM,
                    SimulationType.LEAGUE_CAREER
            );
        }
        leagueSystem.clear();
        leagueSystem.addAll(List.of(platinum, diamond, champion));

        printer.newLine();
        printer.printMessage("THE NEW LEAGUE TIERS");
        printer.newLine();

        printer.printLeagueSystem(leagueSystem);

        while (true) {

            season.playSeason(leagueSystem, GameConstants.DEFAULT_LEAGUE_SIZE, settings, SimulationType.LEAGUE_CAREER, true);

            LeagueResult platinumResult =
                    handlePromotion(platinum, LeagueTier.PLATINUM);

            printer.newLine();

            LeagueResult diamondResult =
                    handlePromotion(diamond, LeagueTier.DIAMOND);

            printer.newLine();

            LeagueResult championResult =
                    handlePromotion(champion, LeagueTier.CHAMPION);

            printer.newLine();

            printer.printMessage("Time for the Grand Championship playoffs!!!");
            printer.askedToPressEnter();
            input.pressEnter();

            result = grandChampKnockouts.runKnockout(championResult.promoted(), settings, SimulationType.LEAGUE_CAREER);

            int seasonNumber = hallOfFame.getSeasonNumber();

            GrandChampionPodium podium = new GrandChampionPodium(
                    seasonNumber,
                    result.champion().getName(),
                    result.champion().getRating(),

                    result.runnerUp().getName(),
                    result.runnerUp().getRating(),

                    result.thirdPlace().getName(),
                    result.thirdPlace().getRating()
            );
            hallOfFame.saveResult(podium);

            printer.printGrandChampion(result.champion());

            updateAllLeagues(championResult, diamondResult, platinumResult);

            saveData = saveMapper.createSaveData(
                    champion,
                    diamond,
                    platinum,
                    hallOfFame.getHistory()
            );

            menu.show(true, saveData);

            printer.printLeagueSystem(leagueSystem);
        }
    }

    /**
     * Handles promotion and relegation for a single league tier.
     *
     * <p>Calculates which teams move between divisions and displays the
     * results to the user.</p>
     *
     * @param league the league being processed
     * @param tier the league tier being displayed
     * @return the promotion and relegation results
     */
    private LeagueResult handlePromotion(League league, LeagueTier tier) {

        LeagueResult result =
                leagueManager.promoteAndRelegate(league);

        printer.printLeagueWinnersAndLosers(
                result.promoted(),
                result.relegated(),
                tier,
                settings
        );

        return result;
    }

    /**
     * Applies promotion and relegation changes across all league tiers.
     *
     * <p>Moves promoted teams into higher divisions and relegated teams into
     * lower divisions after the completion of a season. The Grand Champion is
     * passed into each league update to apply championship bonuses.</p>
     *
     * @param championResult results from the Champion league
     * @param diamondResult results from the Diamond league
     * @param platinumResult results from the Platinum league
     */
    private void updateAllLeagues(LeagueResult championResult, LeagueResult diamondResult, LeagueResult platinumResult) {

        Team championTeam = result.champion();

        leagueManager.updateLeague(
                champion,
                championResult.promoted(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
        leagueManager.updateLeague(
                champion,
                diamondResult.promoted(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
        leagueManager.updateLeague(
                diamond,
                championResult.relegated(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
        leagueManager.updateLeague(
                diamond,
                platinumResult.promoted(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
        leagueManager.updateLeague(
                platinum,
                diamondResult.relegated(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
        leagueManager.updateLeague(
                platinum,
                platinumResult.relegated(),
                championTeam,
                SimulationType.LEAGUE_CAREER
        );
    }
}