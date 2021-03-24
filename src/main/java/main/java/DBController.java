package main.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBController {
    private Connection connection;
    private String current_user_email;
    private int studentThreadId;
    private int studentPostId;
    private int instructorPostId;
    public boolean studentThreadIsCreated = false;
    // Instructor privileges are managed in the CLI class

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
                    "AND piazza_user.email=%s;", password, email));

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

    // Return -1 for exception
    // Return 0 for successful thread creation
    public int newThreadAsStudent(String examFolderId, String postDescription, String threadTitle, String courseId) {
        try {
            // 1. create Thread with threadTitle and commit changes.
            PreparedStatement createThreadQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO thread (thread_id, title, course_id) " +
                            "VALUES(DEFAULT, %s, %s);", threadTitle, courseId
            ));
            createThreadQuery.executeUpdate();
            createThreadQuery.close();
            this.connection.commit();

            // 2. retrieve threadId of the created thread (set automatically).
            PreparedStatement retrieveThreadIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrieveThreadIdResult = retrieveThreadIdQuery.executeQuery();

            while (retrieveThreadIdResult.next()) {
                this.studentThreadId = retrieveThreadIdResult.getInt(1);
            }
            retrieveThreadIdQuery.close();

            // 3. create Post using threadId and postDescription. Last_edited auto-update.
            PreparedStatement makePostQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                            "VALUES(DEFAULT, %s, TRUE, NOW(), %s, %s, NULL);", postDescription, current_user_email, String.valueOf(studentThreadId)
            ));
            makePostQuery.executeUpdate();
            makePostQuery.close();
            this.connection.commit();

            // 4. retrieve post_id of created Post.
            PreparedStatement retrievePostIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();

            while (retrievePostIdResult.next()) {
                this.studentPostId = retrievePostIdResult.getInt(1);
            }
            retrievePostIdQuery.close();

            // 5. update the post_id of the created Thread to make the created post the main post.
            PreparedStatement setMainPostQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO main_post (thread_id, post_id) " +
                            "VALUES(%s, %s);", String.valueOf(this.studentThreadId), String.valueOf(this.studentPostId)
            ));
            setMainPostQuery.executeUpdate();
            setMainPostQuery.close();
            this.connection.commit();

            // 6. create PostInFolder using the folderId and postId.
            PreparedStatement setPostFolderQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post_in_folder (post_id, folder_id) "
                    + "VALUES(%s, %s);", String.valueOf(this.studentPostId), String.valueOf(examFolderId)
            ));
            setPostFolderQuery.executeUpdate();
            setPostFolderQuery.close();
            this.connection.commit();

            this.studentThreadIsCreated = true;

            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // TODO: implement
    // Return -1 for exception
    // Return 0 for successful thread reply
    public int replyToThreadAsInstructor(String postDescription, String email) {
        try {
            // 1. create Post using postDescription and the postId + threadId stored as a class variable.
            PreparedStatement createInstructorReplyQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                            "VALUES(DEFAULT, %s, TRUE, NOW(), %s, %s, %s);", postDescription, email, String.valueOf(this.studentThreadId), String.valueOf(this.studentPostId)
            ));
            createInstructorReplyQuery.executeUpdate();
            createInstructorReplyQuery.close();
            this.connection.commit();

            // 2. retrieve the post_id of the created instructor reply
            PreparedStatement retrievePostIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();

            while (retrievePostIdResult.next()) {
                this.instructorPostId = retrievePostIdResult.getInt(1);
            }
            retrievePostIdQuery.close();

            // 3. set created Post as Instructor's answer (Thread attribute).
            PreparedStatement setPostAsInstructorAnswerQuery = this.connection.prepareStatement(String.format(
                    "UPDATE thread SET instructor_answer_id = %s WHERE thread_id = %s;", String.valueOf(this.instructorPostId), String.valueOf(this.studentThreadId)
            ));
            setPostAsInstructorAnswerQuery.executeUpdate();
            setPostAsInstructorAnswerQuery.close();
            this.connection.commit();

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Return -1 for exception
    // Return 0 for successful search
    public int searchForPostByKeyword(String keywordPattern) {
        try {
            // 1. Retrieve all matching posts using the LIKE operator
            PreparedStatement retrieveMatchingPostsQuery = this.connection.prepareStatement(String.format(
                    "SELECT post_id FROM post " +
                            "WHERE post.post_description LIKE %s;", keywordPattern
            ));
            ResultSet retrieveMatchingPostsResult = retrieveMatchingPostsQuery.executeQuery();

            // 2. Print out all matching results
            int counter = 1;
            while (retrieveMatchingPostsResult.next()) {
                // 3. Format: Search result number x: post_id
                System.out.println("\nSearch result number" + String.valueOf(counter) + ":\t" + String.valueOf(retrieveMatchingPostsResult.getInt("post_id")));
                counter++;
            }

            retrieveMatchingPostsQuery.close();

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Return 0 if successful
    // Return -1 if an exception occurred
    public int getUserStatisticsAsInstructor() {
        try {
            // 1. Retrieve user statistics from database
            PreparedStatement retrieveStatisticsQuery = this.connection.prepareStatement(
                    "SELECT stat1.email,stat1.viewCount,stat2.createCount FROM " +
                            "(SELECT piazza_user.email, COUNT(post_read.post_ID) AS viewCount " +
                            "FROM piazza_user LEFT JOIN post_read ON piazza_user.email = post_read.email " +
                            "GROUP BY piazza_user.email) AS stat1 LEFT JOIN  " +
                            "(SELECT piazza_user.email, COUNT(post_create.post_id) AS createCount " +
                            "FROM piazza_user LEFT JOIN post_create ON piazza_user.email = post_create.email " +
                            "GROUP BY piazza_user.email) AS stat2 ON stat1.email = stat2.email " +
                            "ORDER BY viewCount DESC;"
            );

            ResultSet retrieveStatisticsResult = retrieveStatisticsQuery.executeQuery();

            // 2. Print out each user's email, view count and create count
            while (retrieveStatisticsResult.next()) {
                String email = retrieveStatisticsResult.getString("email");
                int viewCount = retrieveStatisticsResult.getInt("viewCount");
                int createCount = retrieveStatisticsResult.getInt("createCount");
                System.out.println("\nUser: " + email + ".");
                System.out.println("\nPosts viewed: " + viewCount);
                System.out.println("\nPosts created: " + createCount + "\n");
            }

            retrieveStatisticsQuery.close();

            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    /*
    private boolean isInstructor(String email) {
        try {
            PreparedStatement isInstructorQuery = this.connection.prepareStatement(String.format(
                    "SELECT email (email, course_id) " +
                            "FROM is_instructor " +
                            "WHERE is_instructor.email=%s;", email
            ));
            ResultSet isInstructorResult = isInstructorQuery.executeQuery();

            if (isInstructorResult.next()) {
                System.out.println(email + " is an instructor.");
                return true;
            }

            else {
                System.out.println(email + " is not an instructor.");
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Could not verify instructor status of " + email + ".");
        }
     */
    }
}
