package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineInterface {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private DBController dbController = new DBController();
    private String studentEmail = "audunrb@icloud.com";
    private String studentPassword = "passord";
    private String instructorEmail = "erikpl@protonmail.com";
    private String instructorPassword = "abc123";
    private String examFolderId = "1";
    private String courseId = "1";
    private String tagId = "1";
    private String postDescription = "Hello! Can you pls explain 4NF? Don't get it :(";
    private String threadTitle = "Q: Stuck on 4NF";
    private String replyDescription = "No MVDs!";
    private String keywordPattern = "%WAL%";

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
            useCase = Integer.parseInt(reader.readLine());

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
            //handleMakePost();
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
    // Return 0 for successful login.
    // Return -1 for exception
    // TODO: implement
    private int handleLogin() {
        try {
            dbController.userLogin(studentEmail, studentPassword);
            return 0;
        }
        catch (Exception e) {
            return -1;
        }
    }


    // A student makes a post belonging to the folder “Exam” and
    // tagged with “Question”.
    // TODO: user feedback
    private int handleMakePost() {
        try {
            // Check if the current user is an instructor
            if (dbController.getCurrentUserEmail().equals(instructorEmail)) {
                // Switch to student account to make post
                dbController.userLogin(studentEmail, studentPassword);
            }
            // Creates new post using hard-coded constants
            dbController.newThreadAsStudent(examFolderId, postDescription, threadTitle, courseId, tagId);

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // An instructor replies to a post belonging to the folder
    // “Exam”. The input to this is the id of the post replied to.
    // This could be the post created in use case 2.
    // TODO: user feedback
    private int handleInstructorReply() {
        try {
            // Check if the current user is a student
            if (dbController.getCurrentUserEmail().equals(studentEmail)) {
                // Switch to instructor account
                dbController.userLogin(instructorEmail, instructorPassword);
            }

            dbController.replyToThreadAsInstructor(replyDescription);

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // A student searches for posts with a specific keyword “WAL”.
    // The return value of this should be a list of ids of posts
    // matching the keyword.
    // TODO: implement
    private int handlePostSearch() {
        try {


            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
