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
import service.engine.TeamFactory;
import domain.dto.GroupResult;
import service.management.LeagueManager;
import ui.ConsolePrinter;
import ui.GameMenu;
import ui.GameSettings;
import ui.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordinates the World Cup simulation lifecycle.
 *
 * <p>This class manages the complete World Cup process, including
 * qualification, group-stage creation and simulation, knockout-stage
 * progression, Grand Championship history, and save/load functionality.</p>
 *
 * <p>The simulator delegates individual responsibilities to specialised
 * classes such as {@link TeamFactory}, {@link LeagueManager},
 * {@link Season}, and {@link KnockoutStage}, keeping the overall simulation
 * flow separate from the underlying domain and calculation logic.</p>
 */
public class WorldCupSimulator {

    private final List<Team> teamList;
    private final List<League> leagueSystem;
    private final KnockoutStage knockoutRounds;
    private final Season season;
    private final TeamFactory teamFactory;
    private final LeagueManager leagueManager;
    private final ConsolePrinter printer;
    private final GameSettings settings;
    private final HallOfFame hallOfFame;
    private final SaveMapper saveMapper;
    private final Input input;
    private final GameMenu<WorldCupSaveData> menu;

    /**
     * Creates a World Cup simulator using the specified simulation type and
     * supplied console presentation and input components.
     *
     * <p>The constructor initialises the components required to run the
     * simulation, including team data, league management, season handling,
     * knockout stages, persistence, and user interaction.</p>
     *
     * @param type simulation type used to determine the appropriate team data
     *             and save directory
     * @param printer handles console output and presentation
     * @param input handles console user input
     */
    public WorldCupSimulator(
            SimulationType type,
            ConsolePrinter printer,
            Input input) {

        this.printer = printer;
        this.input = input;

        knockoutRounds = new KnockoutStage(printer, input);
        season = new Season(printer, input);

        teamFactory = new TeamFactory();
        leagueManager = new LeagueManager();
        leagueSystem = new ArrayList<>();
        settings = new GameSettings();

        hallOfFame = new HallOfFame();
        saveMapper = new SaveMapper();

        SaveGame<WorldCupSaveData> saveGame =
                new SaveGame<>(type, WorldCupSaveData.class);

        menu = new GameMenu<>(
                printer,
                saveGame,
                hallOfFame,
                input
        );
        teamList = teamFactory.createDefaultTeams(type);
    }

    /**
     * Starts and manages the World Cup simulation.
     *
     * <p>The simulation begins with the qualification stage, followed by
     * the World Cup group stage and knockout stage. Once the knockout stage
     * is completed, the final podium is recorded in the Hall of Fame and
     * the qualifying teams are prepared for the next World Cup cycle.</p>
     *
     * <p>The method also handles loading existing saves, configuring
     * cinematic mode, and saving the current World Cup state.</p>
     */
    public void start() {
        printer.printMessage("!!!WORLD CUP SIMULATOR!!!");

        League qualifiers = new League(
                new ArrayList<>(teamList),
                LeagueTier.QUALIFIERS,
                SimulationType.WORLD_CUP);

        WorldCupSaveData saveData = menu.show(false, null);
        boolean saveLoaded = saveData != null;

        printer.printMessage("Enable cinematic mode? (yes/no)");

        String answer = input.userInput().trim();
        settings.setCinematicMode(answer.equalsIgnoreCase("yes"));

        if (saveLoaded) {
            qualifiers.getTeams().clear();
            qualifiers = saveMapper.restoreWorldCup(saveData);

            hallOfFame.restore(saveData.history());
        }

        while (true) {

            leagueSystem.clear();
            leagueSystem.add(qualifiers);

            season.playSeason(leagueSystem, GameConstants.WORLD_CUP_QUALIFIER_SIZE, settings, SimulationType.WORLD_CUP, true);

            List<Team> qualifiedTeams = new ArrayList<>(qualifiers.getTeams().subList(0, 48));

            Collections.shuffle(qualifiedTeams);

            leagueSystem.clear();
            leagueSystem.addAll(teamFactory.createWorldCupGroups(qualifiedTeams));

            printer.newLine();
            printer.printMessage("WORLD CUP GROUP STAGE!");
            printer.newLine();

            printer.printLeagueSystem(leagueSystem);

            season.playSeason(leagueSystem, GameConstants.GROUP_STAGE_TABLE_SIZE, settings, SimulationType.WORLD_CUP, false);

            KnockoutResult result = knockoutRounds.runKnockout(handleGroupPromotion(leagueSystem), settings, SimulationType.WORLD_CUP);

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

            leagueManager.updateLeague(qualifiers, teamList, result.champion(), SimulationType.WORLD_CUP);

            saveData = saveMapper.createWorldCupSaveData(
                    teamList,
                    hallOfFame.getHistory()
            );

            menu.show(true, saveData);
        }
    }

    /**
     * Determines which teams progress from the group stage into the
     * knockout stage.
     *
     * <p>The winners and runners-up from each group are distributed into
     * two knockout bracket sides. The eight highest-ranked third-place
     * teams are then added to the brackets to complete the knockout-stage
     * qualification.</p>
     *
     * <p>Third-place teams are ranked using their league performance before
     * the top eight are selected.</p>
     *
     * @param groups completed World Cup group-stage leagues
     * @return teams qualified for the knockout stage, arranged into two
     * bracket sides
     */
    private List<Team> handleGroupPromotion(List<League> groups) {

        List<Team> sideOne = new ArrayList<>();
        List<Team> sideTwo = new ArrayList<>();
        List<Team> thirdPlace = new ArrayList<>();

        List<Team> qualifiedTeams = new ArrayList<>();

        for (int i = 0; i < groups.size(); i++) {

            GroupResult result = leagueManager.getGroupStageResult(groups.get(i));

            if (i % 2 == 0) {
                sideOne.add(result.winner());
                sideTwo.add(result.runnerUp());
            } else {
                sideOne.add(result.runnerUp());
                sideTwo.add(result.winner());
            }

            thirdPlace.add(result.thirdPlace());
        }

        leagueManager.sortTeams(thirdPlace, false);

        List<Team> thirdPlaceQualifiers =
                new ArrayList<>(thirdPlace.subList(0, 8));

        for (int i = 0; i < thirdPlaceQualifiers.size(); i++) {

            if (i % 2 == 0) {
                sideOne.add(thirdPlaceQualifiers.get(i));
            } else {
                sideTwo.add(thirdPlaceQualifiers.get(i));
            }
        }
        qualifiedTeams.addAll(sideOne);
        qualifiedTeams.addAll(sideTwo);

        return qualifiedTeams;
    }
}
