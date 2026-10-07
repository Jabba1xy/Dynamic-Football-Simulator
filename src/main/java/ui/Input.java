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
     */
    public void pressEnter() {
        scanner.nextLine();
    }

    /**
     * Reads a line of text entered by the user.
     *
     * @return the user-provided console input
     */
    public String userInput() {
        return scanner.nextLine();
    }
}