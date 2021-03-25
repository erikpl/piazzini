package main.java;

// Runs the application
public class RunApp {
    public static void main(String... args) {
        int state = 0;
        CommandLineInterface cli = new CommandLineInterface();

        // Run until the user exits the application
        while (state != 1) {
            state = cli.selectUseCase();
        }
    }
}
