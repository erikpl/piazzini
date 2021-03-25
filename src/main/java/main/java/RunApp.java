package main.java;

// Runs the application
public class RunApp {
    public static void main(String... args) {
        // Keep track of the state of the application
        // Begins in the successful 0 state
        int state = 0;
        // The CLI instance corresponds to an instance of the application
        CommandLineInterface cli = new CommandLineInterface();

        // Run until the user exits the application
        while (state != 1) {
            state = cli.selectUseCase();
        }
    }
}
