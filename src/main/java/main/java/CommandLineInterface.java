package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineInterface {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final DBController dbController = new DBController();
    private final String studentEmail = "audunrb@icloud.com";
    private final String studentPassword = "passord";
    private final String instructorEmail = "erikpl@protonmail.com";
    private final String instructorPassword = "abc123";
    private final String examFolderId = "1";
    private final String courseId = "1";
    private final String tagId = "1";
    private final String postDescription = "Hello! Can you pls explain 4NF? Don't get it :(";
    private final String threadTitle = "Q: Stuck on 4NF";
    private final String replyDescription = "No MVDs!";
    private final String keywordPattern = "%WAL%";
    private boolean studentPostDone = false;

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
                + "\nWrite 0 to quit.\n"
        );

        try {
            int useCase;
            useCase = Integer.parseInt(reader.readLine());

            if (useCase == 0) {
                System.out.println("Exiting application...");
                return 1;
            }

            else if (useCase >= 1 && useCase <= 5) {
                return handleValidUseCase(useCase);
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

    // Prints out different user feedback for different unwanted events
    private int handleInvalidUseCase(boolean exception) {
        if (exception) {
            System.out.println("Something went wrong.");
        }
        else {
            System.out.println("Your input was invalid.");
        }
        return -1;
    }

    // Executes corresponding functions based on the selected use case
    private int handleValidUseCase(int useCase) {
        if (useCase == 1) {
            return handleLogin();
        }

        else if (useCase == 2) {
            return handleMakePost();
        }

        else if (useCase == 3) {
            return handleInstructorReply();
        }

        else if (useCase == 4) {
            return handlePostSearch();
        }

        // useCase == 5
        else {
            return handleViewStatistics();
        }

    }

    // A student logs into the system, i.e., check user name
    // and password. No encryption.
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
    private int handleMakePost() {
        try {
            // Check if the current user is an instructor
            if (dbController.getCurrentUserEmail().equals(instructorEmail)) {
                // Switch to student account to make post
                System.out.println("Switching to student account.");
                dbController.userLogin(studentEmail, studentPassword);
            }
            // Creates new post using hard-coded constants
            dbController.newThreadAsStudent(examFolderId, postDescription, threadTitle, courseId, tagId);

            // Reply can only be posted if the original posts exists
            studentPostDone = true;

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
    
    private int handleInstructorReply() {
        if (!studentPostDone) {
            System.out.println("Cannot reply to a student post as an instructor before said post exists.");
            return -1;

        }
        try {
            // Check if the current user is a student
            if (dbController.getCurrentUserEmail().equals(studentEmail)) {
                // Switch to instructor account
                System.out.println("Switching to instructor account.");
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
    private int handlePostSearch() {

        try {
            System.out.println("Searching for posts containing the keyword \"WAL\"");
            dbController.searchForPostByKeyword(keywordPattern);
            System.out.println("Done!\n");

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // An instructor views statistics for users and how many post
    // they have read and how
    private int handleViewStatistics() {
        try {
            // Check if the current user is a student
            if (dbController.getCurrentUserEmail().equals(studentEmail)) {
                // Switch to instructor account
                dbController.userLogin(instructorEmail, instructorPassword);
            }
            dbController.getUserStatisticsAsInstructor();

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
