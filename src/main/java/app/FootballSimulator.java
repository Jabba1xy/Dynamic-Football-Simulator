package app;

import domain.enums.SimulationType;
import simulations.TableLeagueSimulator;
import simulations.WorldCupSimulator;
import ui.ConsolePrinter;
import ui.Input;

/**
 * Main application controller for the football simulation program.
 *
 * <p>This class provides the entry point for user interaction after the
 * application starts. It displays the simulation menu, processes user
 * selections, and launches the appropriate simulation.</p>
 *
 * <p>Individual simulations are handled by their own dedicated classes,
 * allowing this class to focus only on application flow and navigation.</p>
 */
public class FootballSimulator {

    private final ConsolePrinter printer;
    private final Input input;

    /**
     * Creates the main application controller using the supplied console
     * presentation and input components.
     *
     * <p>Dependencies are supplied externally so that application flow remains
     * separate from the creation of user-interface components and can be tested
     * independently.</p>
     *
     * @param printer handles console output and menu presentation
     * @param input handles console user input
     */
    public FootballSimulator(ConsolePrinter printer, Input input) {
        this.printer = printer;
        this.input = input;
    }

    /**
     * Starts the football simulator application.
     *
     * <p>The user is repeatedly presented with simulation options until they
     * choose to exit. Selected simulations are delegated to their respective
     * simulator classes.</p>
     */
    public void run() {

        boolean running = true;

        while(running) {

            printer.printSimulatorMenu();

            SimulationType type = getSimulationType(
                    input.userInput().trim()
            );

            switch(type) {

                case LEAGUE_CAREER ->
                        new TableLeagueSimulator(SimulationType.LEAGUE_CAREER, printer, input).start();
                case WORLD_CUP ->
                        new WorldCupSimulator(SimulationType.WORLD_CUP, printer, input).start();
                case EXIT -> running = false;
                case INVALID -> printer.printMessage("Invalid option!");
            }
        }
    }

    private SimulationType getSimulationType(String choice) {

        return switch (choice) {
            case "1" -> SimulationType.LEAGUE_CAREER;
            case "2" -> SimulationType.WORLD_CUP;
            case "3" -> SimulationType.EXIT;
            default -> SimulationType.INVALID;
        };
    }
}