package app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ui.ConsolePrinter;
import ui.Input;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FootballSimulatorTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void runWithExitOptionStopsApplication() {

        setInput("3\n");

        FootballSimulator simulator = new FootballSimulator(
                new ConsolePrinter(), new Input());

        simulator.run();

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("FOOTBALL SIMULATOR"));
    }
    @Test
    void invalidOptionDisplaysErrorMessage() {

        setInput("invalid\n3\n");

        FootballSimulator simulator = new FootballSimulator(
                new ConsolePrinter(), new Input());

        simulator.run();

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("Invalid option!"));
    }
    private void setInput(String input) {

        System.setIn(
                new ByteArrayInputStream(input.getBytes())
        );
        output = new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));
    }
}