package ui;

import java.util.Scanner;

/**
 * Handles console user input for the football simulation.
 *
 * <p>Provides methods for reading user input and waiting for the user
 * to press ENTER. The scanner is maintained by each {@code Input} instance,
 * allowing input handling to be passed to other classes as a dependency.</p>
 */
public class Input {

    private final Scanner scanner;

    /**
     * Creates an input handler using the standard system input stream.
     */
    public Input() {
        scanner = new Scanner(System.in);
    }

    /**
     * Waits until the user presses ENTER.
     *
     * @return {@code true} if a line was consumed, or {@code false}
     *         if no more input is available
     */
    public boolean pressEnter() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
            return true;
        }
        return false;
    }

    /**
     * Reads a line of text entered by the user.
     *
     * @return the user-provided console input, or {@code null}
     *         if no more input is available
     */
    public String userInput() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }
}