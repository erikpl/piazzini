package main.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBController {
    private Connection connection;
    private String current_user_email;
    private String current_course_id;
    private String studentThreadId;
    private String studentPostId;
    // TODO: determine how to manage Instructor privileges

    // Empty constructor
    public DBController() {
    }

    // Method for connecting to the MySQL server
    public void connect() {
        try {
            // Properties object to store DB user credentials
            Properties credentials = new Properties();
            credentials.put("user", "root");
            credentials.put("password", "12345678");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/piazzini?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false",credentials);
        }
        // If unable to connect to the MySQL server
        catch (Exception e) {
            throw new RuntimeException("Unable to connect", e);
        }
    }

    // Return -1 for exception
    // Return 0 for successful login
    // Return 1 for invalid credentials
    // TODO: test method
    public int userLogin(String email, String password) {
        try {
            PreparedStatement query = this.connection.prepareStatement(String.format(
                    "SELECT email FROM piazza_user " +
                    "WHERE piazza_user.password=%s " +
                    "AND piazza_user.email=%s", password, email));

            ResultSet queryResult = query.executeQuery();

            if (queryResult.next()) {
                this.current_user_email = queryResult.getString("email");
                System.out.println("You are now logged in as " + this.current_user_email);
                return 0;
            }
            else {
                System.out.println("The username and/or password is wrong. Please try again.");
                return 1;
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // TODO: determine if folderId should be an int instead of a String
    // TODO: implement
    // User email and current course is stored as a class variable
    // Return -1 for exception
    // Return 0 for successful thread creation
    // Return 1 for invalid folderId
    public int newThreadAsStudent(String folderId, String postDescription, String threadTitle) {
        // 1. create Thread with threadTitle.
        // 2. retrieve threadId of the created thread (set automatically).
        // 3. create Post using threadId and postDescription. Last_edited auto-update.
        // 4. retrieve post_id of created Post.
        // 5. update the post_id of the created Thread to make the created post the main post.
        // 6. create PostInFolder using the folderId and postId.
        // 7. create MainPost entry (may make other parts of this code redundant).
    }

    // TODO: implement
    // Return -1 for exception
    // Return 0 for successful thread reply
    // Return 1 for invalid threadId
    public int replyToThreadAsInstructor(String postDescription) {
        // 1. log in as an Instructor.
        // 2. create Post using postDescription and the postId + threadId stored as a class variable.
        // 3. set created Post as Instructor's answer (Thread attribute).
    }

    // Return -1 for exception
    // Return 0 for successful search
    // Return 1 for empty result
    // TODO: determine if 1 for empty result is the ideal method
    public int searchForPostByKeyword(String keyword) {
        // 1.
    }
}
