package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineInterface {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    // Return 0 for OK
    // Return -1 for invalid
    // Return 1 for exit
    public int selectUseCase() {
        // Prompt user to select a use case, each corresponding to a user story
        System.out.println(
                "Choose a use case from 1-5:"
                + "\n1: Log into system as a student."
                + "\n2: Make a post as a student."
                + "\n3: Reply to post as an instructor."
                + "\n4: Search for posts as a student."
                + "\n5: View user statistics as an instructor."
                + "\nPress 0 to quit.\n"
        );

        try {
            int useCase;
            useCase = Integer.parseInt(this.reader.readLine());

            if (useCase == 0) {
                System.out.println("Exiting application...0");
                return 1;
            }

            else if (useCase >= 1 && useCase <= 5) {
                handleValidUseCase(useCase);
                return 0;
            }

            else {
                return handleInvalidUseCase(false);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            return handleInvalidUseCase(true);
        }
    }

    private int handleInvalidUseCase(boolean exception) {
        if (exception) {
            System.out.println("Something went wrong.");
        }
        else {
            System.out.println("Your input was invalid.");
        }
        return -1;
    }

    private void handleValidUseCase(int useCase) {
        // All common functionality for all use cases, such as database connection
        // TODO: Connect to database through controller
        if (useCase == 1) {
            handleLogin();
        }

        if (useCase == 2) {
            handleMakePost();
        }

        if (useCase == 3) {
            handleInstructorReply();
        }

        if (useCase == 4) {
            handlePostSearch();
        }

        if (useCase == 5) {
            handleViewStatistics();
        }
    }

    // A student logs into the system, i.e., check user name
    // and password. No encryption.
    // TODO: implement
    private int handleLogin() {
        System.out.println("Handling login...");
        return 0;
    }

    // A student makes a post belonging to the folder “Exam” and
    // tagged with “Question”.
    // TODO: implement
    private int handleMakePost() {
        System.out.println("Creating post...");
        return 0;
    }

    // An instructor replies to a post belonging to the folder
    // “Exam”. The input to this is the id of the post replied to.
    // This could be the post created in use case 2.
    // TODO: implement
    private int handleInstructorReply() {
        System.out.println("Replying as instructor...");
        return 0;
    }

    // A student searches for posts with a specific keyword “WAL”.
    // The return value of this should be a list of ids of posts
    // matching the keyword.
    // TODO: implement
    private int handlePostSearch() {
        System.out.println("Searching post...");
        return 0;
    }

    // An instructor views statistics for users and how many post
    // they have read and how
    // TODO: implement
    private int handleViewStatistics() {
        System.out.println("Viewing stats...");
        return 0;
    }

    // TODO: implement similar functionality in a dedicated class
    public static void main(String... args) {
        int state = 0;
        CommandLineInterface cli = new CommandLineInterface();

        while (state == 0 || state == -1) {
            state = cli.selectUseCase();
        }
    }
}
