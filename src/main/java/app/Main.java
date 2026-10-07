package app;

import ui.ConsolePrinter;
import ui.Input;

public class Main {

    public static void main(String[] args) {
        new FootballSimulator(new ConsolePrinter(), new Input()).run();
    }
}